package es.meriland.chat.commands;

import es.meriland.chat.MeriChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TCommand extends Command {

    private static MeriChat plugin;
    
    public TCommand(MeriChat This) {
        super("tell", "merichat.tell");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        
        if(args.length == 0){
            sender.sendMessage("test");
            return;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        final ProxiedPlayer p = (ProxiedPlayer) sender;
        if (target == null){
            
            return;
        }
        String mensaje = "";
        for (int i = 0; i < args.length; i++) {
            mensaje = mensaje + args[i] + " ";
        }
        String mensajeFinal = mensaje;
        target.sendMessage(mensajeFinal);
        p.sendMessage("Has enviado un mensaje.");
    }
    
}
