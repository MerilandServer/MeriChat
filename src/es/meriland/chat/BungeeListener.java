package es.meriland.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.logging.Level;
import java.io.DataInputStream;
import net.md_5.bungee.api.ChatColor;

public class BungeeListener implements Listener {
    
    private final MeriChat plugin;
    
    public BungeeListener(MeriChat instance) {
        plugin = instance;
    }

    public void sendChat(ProxiedPlayer player, String mensaje) {
        try {
            BaseComponent[] msg = Parser.parse(mensaje);
            plugin.getProxy().getPlayers().forEach(target -> target.sendMessage(msg));
        } catch (Throwable th) {
            player.sendMessage(Parser.parse("&cError interno procesando el mensaje."));
            plugin.getLogger().log(Level.SEVERE, "Error procensando el mensaje", th);
        }
    }
    
    public void sendPrivate(String targetS, String fromS, String mensaje) {
        ProxiedPlayer from = plugin.getProxy().getPlayer(fromS);
        if (from == null) return;
        
        ProxiedPlayer target = plugin.getProxy().getPlayer(targetS);
        if (target == null) {
            from.sendMessage(Parser.parse(ChatColor.RED + "¡Jugador no encontrado!"));
            return;
        }
        String msg = c("[&c" + fromS + " &f-> &d" + targetS + "&f]: " + mensaje);
        target.sendMessage(Parser.parse(msg));
        from.sendMessage(Parser.parse(msg));        
    }
    
    /*
     * Recibir los packets de Bukkit con rangos y demás datos 
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equals(MeriChat.MAIN_CHANNEL)) {
            event.setCancelled(true);
            if (event.getReceiver() instanceof ProxiedPlayer && event.getSender() instanceof Server) {
                try {
                    ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                    String subchannel = in.readUTF();
                    if (subchannel.equals(MeriChat.MAIN_SUBCHANNEL)) {
                        sendChat(player, in.readUTF());
                    } else if (subchannel.equals(MeriChat.PRIVATE_SUBCHANNEL)) {
                        sendPrivate(in.readUTF(), in.readUTF(), in.readUTF());
                    }
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Error obtieniendo datos de Bukkit", ex);
                }
            }
        }
    }
    
    public String c(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
