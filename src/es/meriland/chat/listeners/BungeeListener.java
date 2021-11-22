package es.meriland.chat.listeners;

import es.meriland.chat.MeriChat;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

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
        if (!event.isCommand() && event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
            if (MeriChat.activeChats.get(sender.getUniqueId()) == null) {
                plugin.processPublicMessage(sender, event.getMessage().split(" "));
            } else {
                plugin.processPrivateMessage(MeriChat.activeChats.get(sender.getUniqueId()), sender, event.getMessage());
            }

            event.setCancelled(true);
        }
    }
}
