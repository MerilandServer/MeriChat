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
    
    public TCommand(MeriChat instance) {
        super("tell", "merichat.tell", "w", "msg");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0){
            sender.sendMessage(MeriChat.parse(ChatColor.LIGHT_PURPLE + "Usa /tell <Usuario> <Mensaje>!"));
            return;
        }
        
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        final ProxiedPlayer p = (ProxiedPlayer) sender;
        if (target == null){
            sender.sendMessage(BungeeListener.parse(ChatColor.LIGHT_PURPLE + "&4Jugador no encontrado o no conectado!"));
            return;
        }
        
        if(target.getName().equals(sender.getName())){
            sender.sendMessage(BungeeListener.parse(ChatColor.LIGHT_PURPLE + "&4No puedes enviarte mensajes a ti mismo!"));
            return;
        }

        String mensaje = "";
        for (int i = 1; i < args.length; i++) {
            mensaje = mensaje + args[i] + " ";
        }
        String mensajeFinal = mensaje;
        
        target.sendMessage(MeriChat.parse("De " + sender.getName() + ChatColor.LIGHT_PURPLE + ": " + mensajeFinal));
        p.sendMessage(MeriChat.parse("Para " + target.getName() + ChatColor.LIGHT_PURPLE + ": " + mensajeFinal));

        MeriChat.replyTarget.put(target.getUniqueId(), p.getUniqueId());
    } 
}
