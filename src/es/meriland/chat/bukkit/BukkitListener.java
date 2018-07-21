package es.meriland.chat.bukkit;

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
        
    private final BukkitPlugin bukkitPlugin;
    
    public BukkitListener(BukkitPlugin instance) {
        bukkitPlugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        
        String texto = MeriChat.SINTAXIS + MeriChat.CHAR + event.getMessage();
        try {
            processChatMessage(event.getPlayer(), texto);
        } catch (IOException ex) {
            event.getPlayer().sendMessage(c("&cError interno procesando el mensaje"));
            ex.printStackTrace();
        }
        event.setCancelled(true);
    }
    
    private void processChatMessage(Player player, String text) throws IOException {
        if (text.contains("%group%")){
            text = text.replace("%"  + "group%", c(bukkitPlugin.getGroup(player)));
        }
        if (text.contains("%prefix%")){
            text = text.replace("%" + "prefix%", c(bukkitPlugin.getPrefix(player)));
        }
        if (text.contains("%suffix%")){
            text = text.replace("%" + "suffix%", c(bukkitPlugin.getSuffix(player)));
        }
        if (text.contains("%displayName%")){
            text = text.replace("%" + "displayName%", c(player.getDisplayName()));
        }
        
        if (player.hasPermission("MeriChat.colores")) {
            text = c(text);
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
            
        out.writeUTF(MeriChat.MAIN_SUBCHANNEL);
        out.writeUTF(text);
        out.flush();
        out.close();
        
        player.sendPluginMessage(bukkitPlugin, MeriChat.MAIN_CHANNEL, b.toByteArray());
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
