package es.meriland.chat.commands;

import es.meriland.chat.MeriChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class TellCMD extends Command implements TabExecutor {

    private static MeriChat plugin;

    public TellCMD(MeriChat instance) {
        super("tell", null, "msg");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender commandSender, String... strings) {
        if (!(commandSender instanceof ProxiedPlayer)) return;

        if (strings.length != 0 && commandSender.getName().equalsIgnoreCase(strings[0])) {
            plugin.promptError((ProxiedPlayer) commandSender, "No puedes mandarte mensajes a ti mismo");
            return;
        }

        switch (strings.length) {
            case 0:
                plugin.endPrivateChat((ProxiedPlayer) commandSender);
                break;
            case 1:
                plugin.startPrivateChat((ProxiedPlayer) commandSender, plugin.getProxy().getPlayer(strings[0]));
                break;
            default:
                plugin.processPrivateMessage(null, (ProxiedPlayer) commandSender, strings);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        List<String> strl = new ArrayList<>();
        if (strings.length == 1) {
            plugin.getProxy().matchPlayer(strings[0]).forEach((pl) -> strl.add(pl.getName()));
        }

        return strl;
    }
}
