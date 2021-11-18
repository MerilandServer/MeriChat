package es.meriland.chat.bukkit;

import es.meriland.chat.MeriChat;
import es.meriland.chat.bukkit.commands.IgnoradosCommand;
import es.meriland.chat.bukkit.commands.IgnoreCommand;
import es.meriland.chat.bukkit.commands.RCommand;
import es.meriland.chat.bukkit.commands.TCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class BukkitPlugin extends JavaPlugin {
 
    private BukkitListener bukkitListener;

    public static Permission permission = null;
    public static Chat chat = null;

    @Override
    public void onEnable() {
        bukkitListener = new BukkitListener(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, MeriChat.MAIN_CHANNEL);
        getServer().getPluginManager().registerEvents(bukkitListener, this);
        getCommand("tell").setExecutor(new TCommand(this));
        getCommand("r").setExecutor(new RCommand(this));
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("ignorados").setExecutor(new IgnoradosCommand(this));
        

        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            setupVault();
        }
    }

    private void setupVault() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);

        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
    }

    public String getGroup(Player player) {
        try {
            if (permission != null && permission.getPrimaryGroup(player) != null) {
                return permission.getPrimaryGroup(player);
            }
        } catch (UnsupportedOperationException ignored) {
        }
        return "Miembro";
    }

    public String getPrefix(Player player) {
        if (chat != null) {
            return chat.getPlayerPrefix(player);
        }
        return "&r";
    }

    public String getSuffix(Player player) {
        if (chat != null) {
            return chat.getPlayerSuffix(player);
        }
        return "&r";
    }
    
    public void sendPrivateMessage(String target, String from, String mensaje) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        out.writeUTF(MeriChat.PRIVATE_SUBCHANNEL);
        out.writeUTF(target);
        out.writeUTF(from);
        out.writeUTF(mensaje);

        Collection<? extends Player> c = Bukkit.getOnlinePlayers(); //Usar jugador falso
        Player p = (Player) c.toArray()[0];
        p.sendPluginMessage(this, MeriChat.MAIN_CHANNEL, b.toByteArray());
    }
    
    public void ignore(String target, String from) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        out.writeUTF(MeriChat.IGNORE_SUBCHANNEL);
        out.writeUTF(target);
        out.writeUTF(from);

        Collection<? extends Player> c = Bukkit.getOnlinePlayers(); //Usar jugador falso
        Player p = (Player) c.toArray()[0];
        p.sendPluginMessage(this, MeriChat.MAIN_CHANNEL, b.toByteArray());
    }
}
