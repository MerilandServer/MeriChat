package es.meriland.chat;

import java.io.DataInputStream;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeListener implements Listener {
    
    private final MeriChat plugin;
    
    ConcurrentHashMap<Integer, String> buffer = new ConcurrentHashMap<>();
    int cnt = 0;
    
    public BungeeListener(MeriChat instance) {
        plugin = instance;
    }
    
    /*
     * Evento de Chat en todos los servidores
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final ChatEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        if (event.isCommand()) return;

        event.setCancelled(true);
        plugin.getProxy().getLogger().info("cancelado");

        plugin.getProxy().getScheduler().runAsync(plugin, () -> sendChat((ProxiedPlayer) event.getSender(), event.getMessage()));
    }
    
    public void sendChat(ProxiedPlayer player, String mensaje) {
        try {
            String texto = /*"[%group%]*/" %displayName%: " + mensaje;
            texto = replaceVariables(player, texto);

            BaseComponent msg = parse(texto);
            plugin.getProxy().getPlayers().forEach(target -> target.sendMessage(msg));
        } catch (Throwable th) {
            try {
                player.sendMessage(parse("&cError interno procesando el mensaje."));
            } catch (Throwable ignored) {}
            plugin.getLogger().log(Level.SEVERE, "Error procensando el mensaje", th);
        }
    }
    
    public static BaseComponent parse(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        return new TextComponent(text);
    }
    
    /*
     * Recibir los packets de Bukkit con rangos y demás datos 
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessage(PluginMessageEvent event) {
        plugin.getProxy().getLogger().info("He recibido algo... "+event.getTag());
        if (event.getTag().equals("MeriChat")) {
            event.setCancelled(true);
            if (event.getReceiver() instanceof ProxiedPlayer && event.getSender() instanceof Server) {
                try {
                    ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                    String subchannel = in.readUTF();
                    plugin.getProxy().getLogger().info("Buffer recibido");
                    if(subchannel.equals("chat")){
                        buffer.put(in.readInt(), in.readUTF());
                    }

                } catch (IOException ex) {
                    plugin.getLogger().log(Level.SEVERE,
                            "Exception while parsing data from Bukkit", ex);
                }
            }
        }
    }
    
    public String replaceVariables(ProxiedPlayer player, String text) {
        int tries = 0;
        while(text.matches("^.*%"  + "(group|prefix|suffix|tabName|displayName|world)%.*$") && tries < 3){
            plugin.getProxy().getLogger().info("remplazando... "+tries);
            try {
                int id = getId();
                if (buffer.containsKey(id)) buffer.remove(id);
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("chat");
                out.writeUTF(text);
                out.writeInt(id);
                out.flush();
                out.close();
                player.getServer().sendData("MeriChat", b.toByteArray());
                for (int i = 0; i < 10 && !buffer.containsKey(id); i++) {
                    Thread.sleep(100);
                }
                if(buffer.containsKey(id)){
                    text = buffer.get(id);
                    buffer.remove(id);
                    tries = 0;
                    break;
                }
            } catch (IOException | InterruptedException th){
                th.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {}
            }
            tries++;
        }
        if(tries > 0){
            throw new RuntimeException("Error procesando el mensaje de "+player.getName());
        }
        text = text.replace("%" + "server%", player.getServer() != null ? player.getServer().getInfo().getName() : "unknown");
        return text;
    }
    
    private int getId() {
        return cnt++;
    }
}
