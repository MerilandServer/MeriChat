package es.meriland.chat.commands;

import es.meriland.chat.BungeeListener;
import es.meriland.chat.MeriChat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RCommand extends Command {
    
    
    //HashMap de los target del reply
    public static final HashMap<UUID, UUID> replyTargetU = new HashMap();
    
    
    //Variables
    private static MeriChat plugin;
    private static BungeeListener listener;
    private static IgnoreCmd icmand;
    
    //Constructor
    public RCommand(MeriChat This){
        super("r", "merichat.r");
        this.plugin = plugin;
        this.listener = listener;
        this.icmand = icmand;
    }

    //Obtemes el target del reply
    public ProxiedPlayer getReplyTarget(ProxiedPlayer player){
        
        UUID target = this.replyTargetU.get(player.getUniqueId());
        if(target == null){
            return player;
        }
        return plugin.getProxy().getPlayer(target);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        
        //No podemos contestar desde consola
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage(this.listener.parse("No puedes responder desde consola!"));
            return;
        }
        
        //Declaramos el p
        ProxiedPlayer p = (ProxiedPlayer) sender;
        
        //Si usamos solo /r, que muestre el uso
        if(args.length == 0){
            sender.sendMessage(this.listener.parse("Usa /r <mensaje>"));
            return;
        }
        
        //Declaramos el target
        ProxiedPlayer target = this.getReplyTarget(p);
        if(target == null){
            sender.sendMessage(this.listener.parse("Nadie te ha enviado un mensaje!"));
            return;
        }
        
        //Obtenemos el mensjae segun los argumentos
        String text = "";
        for (String arg : args){
            text = text + arg + " ";
        }
        String textoFinal = text;
        
        //Comprobamos si el jugador está ignorado
        if(TCommand.jugadoresignorados.get(target.getUniqueId()).contains(p.getUniqueId())){
            sender.sendMessage("El jugador está ignorado");
            return;
        }
        
        //Respondemos al ultimo mensaje enviado.
        target.sendMessage(this.listener.parse("De " + sender.getName() +ChatColor.LIGHT_PURPLE + ": " + textoFinal));
        sender.sendMessage(this.listener.parse("Para " + sender.getName() +ChatColor.LIGHT_PURPLE + ": " + textoFinal));

    }
    
}
