package es.meriland.chat.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BukkitListener implements Listener, PluginMessageListener {
        
    private final BukkitPlugin bukkitPlugin;
    
    public BukkitListener(BukkitPlugin instance) {
        bukkitPlugin = instance;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("MeriChat")) return;
        
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
	try {
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("chat")) {
                String text = in.readUTF();
                int id = in.readInt(); 
                processChatMessage(player, text, id);
            }
        } catch(IOException e) {
            bukkitPlugin.getLogger().info("Error enviando un mensaje: ");
            e.printStackTrace();
	}
    }
    
    private void processChatMessage(Player player, String text, int id) throws IOException {
        if (text.contains("%" + "group%")){
            text = text.replace("%"  + "group%", c(bukkitPlugin.getGroup(player)));
        }
        if (text.contains("%" + "prefix%")){
            text = text.replace("%" + "prefix%", c(bukkitPlugin.getPrefix(player)));
        }
        if (text.contains("%" + "suffix%")){
            text = text.replace("%" + "suffix%", c(bukkitPlugin.getSuffix(player)));
        }
        if (text.contains("%" + "displayName%")){
            text = text.replace("%" + "displayName%", c(player.getDisplayName()));
        }
        
        if (player.hasPermission("MeriChat.colores")) {
            text = c(text);
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
            
        out.writeUTF("chat");
        out.writeInt(id);
        out.writeUTF(text);
        out.flush();
        out.close();
        
        player.sendPluginMessage(bukkitPlugin, "MeriChat", b.toByteArray());
    }
    
    String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
