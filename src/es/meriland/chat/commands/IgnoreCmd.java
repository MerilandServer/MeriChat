package es.meriland.chat.commands;

import es.meriland.chat.BungeeListener;
import es.meriland.chat.MeriChat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class IgnoreCmd extends Command {

    
    
    private static MeriChat plugin;
    private static BungeeListener listener;
    
    public IgnoreCmd(MeriChat This) {
        super("ignore", "merichat.ignore");
        this.listener = listener;
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        
        //No se puede ignorar desde la consola
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("No puedes ignorar personas desde la consola!");
            return;
        }
        
        //Si no especificamos persona a ignorar
        if (args.length == 0){
            sender.sendMessage("Usa /ignore <jugador");
            return;
        }
        
        //Si no encontramos el jugador
        ProxiedPlayer ignorado = ProxyServer.getInstance().getPlayer(args[0]);
        if(ignorado == null){
            sender.sendMessage("No se ha encontrado al jugador o no está conectado!");
            return;
        }
        
        //Declaramos el p
        ProxiedPlayer p = (ProxiedPlayer)sender;
        
        //Declaramos ArrayList
        ArrayList<UUID> ignoreList = TCommand.jugadoresignorados.get((p.getUniqueId()));
        
        //Si no existe lista de ignorados...
        if(ignoreList == null){
            ignoreList = new ArrayList(1);
        }
        
        //Si la lista de ignorados no tiene el jugador, que se ignore-
        if(!ignoreList.contains(ignorado.getUniqueId())){
            ignoreList.add(ignorado.getUniqueId());
            p.sendMessage("Jugador " + ignorado.getName() + " ha sido ignorado con éxito.");
        }
        
        //Si en cambio, tiene el nombre en la lista, que no se ignore-
        else{
            ignoreList.remove(ignorado.getUniqueId());
            p.sendMessage("Jugador " + ignorado.getName() + " ya no está ignorado");
        }
        
        //Colocamos la lista de ignorados en el hashmap
        TCommand.jugadoresignorados.put(p.getUniqueId(), ignoreList);
    }
    
}
