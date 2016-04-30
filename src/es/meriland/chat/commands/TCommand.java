package es.meriland.chat.commands;

import es.meriland.chat.MeriChat;
import es.meriland.chat.Parser;
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
        if(args.length < 2){
            sender.sendMessage(Parser.parse(ChatColor.LIGHT_PURPLE + "Usa /tell <Usuario> <Mensaje>!"));
            return;
        }
        
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        final ProxiedPlayer p = (ProxiedPlayer) sender;
        if (target == null){
            sender.sendMessage(Parser.parse(ChatColor.RED + "Jugador no encontrado o no conectado!"));
            return;
        }
        
        if(target.getName().equals(sender.getName())){
            sender.sendMessage(Parser.parse(ChatColor.RED + "No puedes enviarte mensajes a ti mismo!"));
            return;
        }

        String mensaje = "";
        for (int i = 1; i < args.length; i++) {
            mensaje = mensaje + args[i] + " ";
        }
        String mensajeFinal = mensaje;
        
        target.sendMessage(Parser.parse("De " + sender.getName() + ChatColor.LIGHT_PURPLE + ": " + mensajeFinal));
        p.sendMessage(Parser.parse("Para " + target.getName() + ChatColor.LIGHT_PURPLE + ": " + mensajeFinal));

        MeriChat.setReply(target.getUniqueId(), p.getUniqueId());
    } 
}
