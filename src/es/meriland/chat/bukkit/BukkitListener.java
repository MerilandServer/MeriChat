package es.meriland.chat.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
		bukkitPlugin.getLogger().info("Mensaje recibido...");
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
            text = text.replace("%"  + "group%", bukkitPlugin.getGroup(player));
        }
        if (text.contains("%" + "prefix%")){
            text = text.replace("%" + "prefix%", bukkitPlugin.getPrefix(player));
        }
        if (text.contains("%" + "suffix%")){
            text = text.replace("%" + "suffix%", bukkitPlugin.getSuffix(player));
        }
        if (text.contains("%" + "tabName%")){
            text = text.replace("%" + "tabName%", player.getPlayerListName());
        }
        if (text.contains("%" + "displayName%")){
            text = text.replace("%" + "displayName%", player.getDisplayName());
        }
        if (text.contains("%" + "world%")){
            text = text.replace("%" + "world%", player.getWorld().getName());
        }
        
        if (player.isOp()) {
            text = ChatColor.translateAlternateColorCodes('&', text);
        }
        
        bukkitPlugin.getLogger().info("Mensaje procesado, reenviando...");
        bukkitPlugin.getLogger().info(text);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
            
        out.writeUTF("chat");
        out.writeInt(id);
        out.writeUTF(text);
        out.flush();
        out.close();
        
        player.sendPluginMessage(bukkitPlugin, "MeriChat", b.toByteArray());
        bukkitPlugin.getLogger().info("Enviado.");
    }
}
