package es.meriland.chat.bukkit.commands;

import es.meriland.chat.bukkit.BukkitPlugin;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TCommand implements CommandExecutor {

    private static BukkitPlugin plugin;

    public TCommand(BukkitPlugin instance) {
        plugin = instance;
        plugin.getCommand("tell").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Usa /tell <Usuario> <Mensaje>!");
            return true;
        }

        String target = args[0];
        if (target.equals(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "No puedes enviarte mensajes a ti mismo!");
            return true;
        }

        String mensaje = "";
        for (int i = 1; i < args.length; i++) {
            mensaje = mensaje + args[i] + " ";
        }

        try {
            plugin.sendPrivateMessage(target, sender.getName(), mensaje);
            if (sender instanceof Player) {
                Player p = (Player) sender;
                BukkitPlugin.setReply(plugin.getServer().getOfflinePlayer(target).getUniqueId(), p.getUniqueId());
            }     
        } catch (IOException ex) {
            sender.sendMessage("Ha ocurrido un error enviando el mensaje");
            plugin.getLogger().log(Level.INFO, "Error enviando un mensaje privado: {0}", ex.toString());
        }
        return true;
    }
}
