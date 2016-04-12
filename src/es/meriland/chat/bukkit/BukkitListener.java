package es.meriland.chat.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            String prefix = in.readUTF();
            int id = in.readInt();
            processChatMessage(player, text, prefix, id);
        } catch (IOException ex) {
            Logger.getLogger(BukkitListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void processChatMessage(Player player, String text, String prefix, int id) throws IOException {
        if (text.contains("%" + prefix + "group%")){
            text = text.replace("%" + prefix + "group%", bukkitPlugin.getGroup(player));
        }
        if (text.contains("%" + prefix + "prefix%")){
            text = text.replace("%" + prefix + "prefix%", bukkitPlugin.getPrefix(player));
        }
        if (text.contains("%" + prefix + "suffix%")){
            text = text.replace("%" + prefix + "suffix%", bukkitPlugin.getSuffix(player));
        }
        if (text.contains("%" + prefix + "tabName%")){
            text = text.replace("%" + prefix + "tabName%", player.getPlayerListName());
        }
        if (text.contains("%" + prefix + "displayName%")){
            text = text.replace("%" + prefix + "displayName%", player.getDisplayName());
        }
        if (text.contains("%" + prefix + "world%")){
            text = text.replace("%" + prefix + "world%", player.getWorld().getName());
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
