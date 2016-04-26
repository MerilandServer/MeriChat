package es.meriland.chat.commands;

import es.meriland.chat.MeriChat;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RCommand extends Command {

    private static MeriChat plugin;

    public RCommand(MeriChat instance){
        super("r", "merichat.r");
        plugin = instance;
    }

    public ProxiedPlayer getReplyTarget(ProxiedPlayer player){
        UUID target = MeriChat.replyTarget.get(player.getUniqueId());
        if(target == null){
            return null;
        }
        return plugin.getProxy().getPlayer(target);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage(MeriChat.parse("No puedes responder desde consola!"));
            return;
        }
        
        ProxiedPlayer p = (ProxiedPlayer) sender;
        ProxiedPlayer target = getReplyTarget(p);
        if(target == null){
            sender.sendMessage(MeriChat.parse("Nadie te ha enviado un mensaje!"));
            return;
        }
        
        if(args.length == 0){
            sender.sendMessage(MeriChat.parse("Usa /r <mensaje>"));
            return;
        }

        String text = "";
        for (String arg : args){
            text = text + arg + " ";
        }
        String textoFinal = text;

        //Respondemos al ultimo mensaje enviado.
        target.sendMessage(MeriChat.parse("De " + sender.getName() +ChatColor.LIGHT_PURPLE + ": " + textoFinal));
        sender.sendMessage(MeriChat.parse("Para " + target.getName() +ChatColor.LIGHT_PURPLE + ": " + textoFinal));
        
        MeriChat.setReply(target.getUniqueId(), p.getUniqueId());
    }
    
}
