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
import net.md_5.bungee.api.event.PlayerDisconnectEvent;

public class BungeeListener implements Listener {
    
    private final MeriChat plugin;
    
    public BungeeListener(MeriChat instance) {
        plugin = instance;
    }

    public void processChat(ProxiedPlayer player, String mensaje) {
        if (plugin.chatsActivados.containsKey(player.getUniqueId())) {
            ProxiedPlayer chat = plugin.getProxy().getPlayer(plugin.chatsActivados.get(player.getUniqueId()));
            sendPrivateMessage(chat, player, mensaje.split("\u00A8")[1]);
        } else {
            sendChat(player, mensaje.replace("\u00A8", ""));
        }
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
    
    public void processPrivateMsg(String targetS, String fromS, String mensaje) {
        ProxiedPlayer from = plugin.getProxy().getPlayer(fromS);
        if (from == null) return;
        
        if (targetS.equals("")) { //terminar chat privado
            if (!checkEndChat(from)) {
                from.sendMessage(Parser.parse(c("&dUsa /tell <Usuario> <Mensaje>!")));
            }
            return;
        }
        ProxiedPlayer target = plugin.getProxy().getPlayer(targetS);
        if (target == null) {
            from.sendMessage(Parser.parse(c("&c¡Jugador no encontrado!")));
            return;
        }
        
        if (mensaje.equals("")) { //Iniciar chat con target   
            if (checkEndChat(from)) return;
            plugin.chatsActivados.put(from.getUniqueId(), target.getUniqueId());
            from.sendMessage(Parser.parse("Has iniciado una conversación privada con "+target.getName()));
            return;
        }
        sendPrivateMessage(target, from, mensaje);   
    }
    
    public boolean checkEndChat(ProxiedPlayer p) {
        if (plugin.chatsActivados.containsKey(p.getUniqueId())) {
            ProxiedPlayer oldChat = plugin.getProxy().getPlayer(plugin.chatsActivados.get(p.getUniqueId()));
            plugin.chatsActivados.remove(p.getUniqueId());
            p.sendMessage(Parser.parse("Has terminado tu chat con "+oldChat.getName()));
            return true;
        }
        return false;
    }
    
    public void sendPrivateMessage(ProxiedPlayer target, ProxiedPlayer from, String mensaje) {
        String msg = c("[&b" + from.getName() + " &f-> &e" + target.getName() + "&f]: &d" + mensaje);
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
                        processChat(player, in.readUTF());
                    } else if (subchannel.equals(MeriChat.PRIVATE_SUBCHANNEL)) {
                        processPrivateMsg(in.readUTF(), in.readUTF(), in.readUTF());
                    }
                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Error obtieniendo datos de Bukkit", ex);
                }
            }
        }
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        if (plugin.chatsActivados.containsKey(p.getUniqueId())) plugin.chatsActivados.remove(p.getUniqueId());
    }
    
    public String c(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
