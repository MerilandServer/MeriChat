package es.meriland.chat.commands;

import es.meriland.chat.BungeeListener;
import es.meriland.chat.MeriChat;
import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RCommand extends Command {
    
    public static final HashMap<UUID, UUID> replyTargetU = new HashMap();
    
    private static MeriChat plugin;
    private static BungeeListener listener;
    
    public RCommand(MeriChat This){
        super("r", "merichat.r");
        this.plugin = plugin;
        this.listener = listener;
    }

    public ProxiedPlayer getReplyTarget(ProxiedPlayer player){
        
        UUID target = this.replyTargetU.get(player.getUniqueId());
        if(target == null){
            return player;
        }
        return plugin.getProxy().getPlayer(target);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage(this.listener.parse("No puedes responder desde consola!"));
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if(args.length == 0){
            sender.sendMessage(this.listener.parse("Usa /r <mensaje>"));
            return;
        }
        ProxiedPlayer target = this.getReplyTarget(p);
        if(target == null){
            sender.sendMessage(this.listener.parse("Nadie te ha enviado un mensaje!"));
            return;
        }
        String text = "";
        for (String arg : args){
            text = text + arg + " ";
        }
        String textoFinal = text;
        target.sendMessage(this.listener.parse("De " + sender.getName() +ChatColor.LIGHT_PURPLE + ": " + textoFinal));
        sender.sendMessage(this.listener.parse("Para " + sender.getName() +ChatColor.LIGHT_PURPLE + ": " + textoFinal));
    }
    
}
