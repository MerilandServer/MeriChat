package es.meriland.chat.listeners;

import es.meriland.chat.BukkitBridge;
import es.meriland.chat.MeriChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BukkitListener implements Listener {

    private final BukkitBridge bukkitPlugin;

    public BukkitListener(BukkitBridge instance) {
        bukkitPlugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        try {
            processChatMessage(event.getPlayer(), event.getMessage());
        } catch (IOException ex) {
            event.getPlayer().sendMessage("&cError interno procesando el mensaje");
            ex.printStackTrace();
        }
        event.setCancelled(true);
    }

    private void processChatMessage(Player player, String text) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        out.writeUTF(MeriChat.MAIN_SUBCHANNEL);
        out.writeUTF(text);
        out.flush();
        out.close();

        player.sendPluginMessage(bukkitPlugin, MeriChat.MAIN_CHANNEL, b.toByteArray());
    }
}