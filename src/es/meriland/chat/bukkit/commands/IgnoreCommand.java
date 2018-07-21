package es.meriland.chat.bukkit.commands;

import es.meriland.chat.bukkit.BukkitPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

public class IgnoreCommand implements CommandExecutor {

    private static BukkitPlugin plugin;
    
    public IgnoreCommand(BukkitPlugin instance) {
        plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("No puedes ignorar desde consola!");
            return true;
        }
        
        Player p = (Player) sender;
        
        if (args.length == 0){
            sender.sendMessage("Usa /ignore <mensaje>");
            return true;
        }
        
        if (args[0].equals(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "¡No puedes ignorarte a ti mismo!");
            return true;
        }
        
        try {
            plugin.ignore(args[0], p.getName());
        } catch (IOException ex) {
            sender.sendMessage("Ha ocurrido un error enviando el mensaje");
            plugin.getLogger().log(Level.INFO, "Error enviando un mensaje privado: {0}", ex.toString());
        }
        return true;
    }
}
