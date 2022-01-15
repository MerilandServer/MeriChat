package es.meriland.chat;

import es.meriland.chat.commands.AdminChatCMD;
import es.meriland.chat.commands.ReplyCMD;
import es.meriland.chat.commands.TellCMD;
import es.meriland.chat.listeners.BungeeListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class MeriChat extends Plugin implements Listener {

    public static final String MAIN_CHANNEL = "merichat:main";
    public static final String MAIN_SUBCHANNEL = "merichat:chat";
    
    public static final HashMap<UUID, UUID> activeChats = new HashMap<>();
    public static final HashMap<UUID, UUID> replyTarget = new HashMap<>();

    private static LuckPerms lpapi;

    @Override
    public void onEnable() {
        lpapi = LuckPermsProvider.get();

        getProxy().registerChannel(MAIN_CHANNEL);

        getProxy().getPluginManager().registerListener(this, new BungeeListener(this));

        getProxy().getPluginManager().registerCommand(this, new TellCMD(this));
        getProxy().getPluginManager().registerCommand(this, new ReplyCMD(this));
    }

    public void startPrivateChat(ProxiedPlayer from, ProxiedPlayer to) {
        if (to == null) {
            promptError(from, "¡Jugador no encontrado!");
            return;
        }

        activeChats.put(from.getUniqueId(), to.getUniqueId());
        promptInfo(from, "Has iniciado una conversación privada con " + to.getName());
    }

    public void endPrivateChat(ProxiedPlayer from) {
        if (activeChats.remove(from.getUniqueId()) != null) {
            promptInfo(from, "Has terminado tu conversación privada");
        } else {
            promptError(from, "No tienes abierta ninguna conversación");
        }
    }

    public void processPublicMessage(ProxiedPlayer from, String... msg) {
        ComponentBuilder builder = new ComponentBuilder();

        CachedMetaData sender = lpapi.getPlayerAdapter(ProxiedPlayer.class).getMetaData(from);

        builder.append(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',sender.getPrefix() + from.getDisplayName() + sender.getSuffix()) + ":"));

        Parser.parse(builder,false, from.hasPermission("chat.colors"), msg);

        getProxy().broadcast(builder.create());
    }

    public void processPrivateMessage(UUID targetUUID, ProxiedPlayer from, String... msg) {
        ProxiedPlayer target;
        if (targetUUID != null) {
            target = getProxy().getPlayer(targetUUID);
        } else {
            target = getProxy().getPlayer(msg[0]);
        }

        if (target == null) {
            // Usuario no encontrado
            promptError(from, "¡Jugador no encontrado!");
            return;
        }

        ComponentBuilder toTarget = new ComponentBuilder("De " + from.getName() + ":");
        ComponentBuilder toSender = new ComponentBuilder("A " + target.getName() + ":");
        toTarget.color(ChatColor.GOLD);
        toSender.color(ChatColor.GOLD);

        BaseComponent[] message = Parser.parse(targetUUID == null, false, msg);
        message[0].setColor(ChatColor.LIGHT_PURPLE);

        target.sendMessage(toTarget.append(message, ComponentBuilder.FormatRetention.FORMATTING).create());
        from.sendMessage(toSender.append(message, ComponentBuilder.FormatRetention.FORMATTING).create());

        replyTarget.put(target.getUniqueId(), from.getUniqueId());
        replyTarget.put(from.getUniqueId(), target.getUniqueId());
    }

    public void processAdminMessage(ProxiedPlayer from, String... msg) {
        ComponentBuilder builder = new ComponentBuilder();

        CachedMetaData sender = lpapi.getPlayerAdapter(ProxiedPlayer.class).getMetaData(from);

        builder.append(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&7[&6Staff&7] &b" + from.getDisplayName() + "&r" + ":")));

        BaseComponent[] message = Parser.parse(false, true, msg);
        message[0].setColor(ChatColor.GREEN);

        BaseComponent[] msgc = builder.append(message, ComponentBuilder.FormatRetention.FORMATTING).create();
        for (ProxiedPlayer target : getProxy().getPlayers()) {
            if (target.hasPermission("chat.adminchat")) target.sendMessage(msgc);
        }
    }

    public void promptInfo(ProxiedPlayer player, String txt) {
        BaseComponent msg = new TextComponent(txt);
        msg.setColor(ChatColor.GREEN);

        player.sendMessage(msg);
    }

    public void promptError(ProxiedPlayer player, String txt) {
        BaseComponent msg = new TextComponent(txt);
        msg.setColor(ChatColor.RED);

        player.sendMessage(msg);
    }
}
