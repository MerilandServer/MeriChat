package es.meriland.chat.listeners;

import es.meriland.chat.MeriChat;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class BungeeListener implements Listener {

    private final MeriChat plugin;

    public BungeeListener(MeriChat instance) {
        plugin = instance;
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        UUID p = event.getPlayer().getUniqueId();
        MeriChat.activeChats.remove(p);
        MeriChat.replyTarget.remove(p);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) return; // Ignore cancelled events
        if (!event.isCommand() && event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
            if (MeriChat.activeChats.get(sender.getUniqueId()) == null) {
                // plugin.processPublicMessage(sender, event.getMessage().split(" "));
                return;
            } else {
                plugin.processPrivateMessage(MeriChat.activeChats.get(sender.getUniqueId()), sender, event.getMessage());
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals(MeriChat.MAIN_CHANNEL)) {
            event.setCancelled(true);
            if (event.getReceiver() instanceof ProxiedPlayer && event.getSender() instanceof Server) {
                try {
                    ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                    String subchannel = in.readUTF();
                    switch (subchannel) {
                        case MeriChat.MAIN_SUBCHANNEL:
                            plugin.processPublicMessage(player, in.readUTF());
                            break;
                    }
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Error obtieniendo datos de Bukkit", ex);
                }
            }
        }
    }
}
