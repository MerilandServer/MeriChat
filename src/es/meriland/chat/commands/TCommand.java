package es.meriland.chat.commands;

import es.meriland.chat.BungeeListener;
import es.meriland.chat.MeriChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TCommand extends Command {

    private static MeriChat plugin;
    private static BungeeListener listener;
    
    public TCommand(MeriChat This) {
        super("tell", "merichat.tell", "w", "r", "msg");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        
        
        //Si no ponemos usuario, que suelte el uso del comando
        if(args.length == 0){
            sender.sendMessage(this.listener.parse("&4Usa /tell <Usuario> <Mensaje!"));
            return;
        }
        
        //Si el jugador no existe.
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        final ProxiedPlayer p = (ProxiedPlayer) sender;
        if (target == null){
            sender.sendMessage(this.listener.parse("&4Jugador no encontrado o no conectado!"));
            return;
        }
        
        //Hacemos que no se puedan enviar mensajes a ti mismo
        if(target.getName() == sender.getName()){
            sender.sendMessage(this.listener.parse("&4No puedes enviarte mensajes a ti mismo!"));
            return;
        }
        
        //Creamos el mensaje
        String mensaje = "";
        for (int i = 1; i < args.length; i++) {
            mensaje = mensaje + args[i] + " ";
        }
        String mensajeFinal = mensaje;
        
        //Mensaje enviado
        target.sendMessage("De " + sender.getName() +ChatColor.LIGHT_PURPLE + ": " + mensajeFinal);
        
        //Mensaje enviado y replicado al sender.
        p.sendMessage("Para " + target.getName() + ChatColor.LIGHT_PURPLE + ": " + mensajeFinal);
    }
    
}
