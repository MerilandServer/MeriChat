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
import java.util.UUID;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;

public class BungeeListener implements Listener {
    
    private final MeriChat plugin;
    
    public BungeeListener(MeriChat instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (MeriChat.replyTarget.containsKey(uuid)) MeriChat.replyTarget.remove(uuid);
    }
    
    public void sendChat(ProxiedPlayer player, String mensaje) {
        try {
            BaseComponent[] msg = Parser.parse(mensaje);
            plugin.getProxy().getPlayers().forEach(target -> target.sendMessage(msg));
        } catch (Throwable th) {
            try {
                player.sendMessage(Parser.parse("&cError interno procesando el mensaje."));
            } catch (Throwable ignored) {}
            plugin.getLogger().log(Level.SEVERE, "Error procensando el mensaje", th);
        }
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
                    if(subchannel.equals(MeriChat.MAIN_SUBCHANNEL)){
                        sendChat(player, in.readUTF());
                    }
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Error obtieniendo los datos de Bukkit", ex);
                }
            }
        }
    }
}
