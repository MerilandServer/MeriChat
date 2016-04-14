package es.meriland.chat.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public void onPluginMessageReceived(String string, Player player, byte[] bytes) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            
            String subchannel = in.readUTF();
            if (!subchannel.equalsIgnoreCase("chat")) return;
            
            String text = in.readUTF();
            int id = in.readInt();
            processChatMessage(player, text, id);
        } catch (IOException ex) {
            Logger.getLogger(BukkitListener.class.getName()).log(Level.SEVERE, null, ex);
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
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataOutputStream outputStream1 = new DataOutputStream(outputStream)) {
            outputStream1.writeUTF("chat");
            outputStream1.writeInt(id);
            outputStream1.writeUTF(text);
            outputStream1.flush();
        }
        player.sendPluginMessage(bukkitPlugin, "MeriChat", outputStream.toByteArray());
    }
}
