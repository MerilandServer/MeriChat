package es.meriland.chat.commands;

import es.meriland.chat.MeriChat;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ReplyCMD extends Command {

    private static MeriChat plugin;

    public ReplyCMD(MeriChat instance) {
        super("reply", null, "r");
        plugin = instance;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) return;

        if (strings.length == 0) {
            plugin.promptError((ProxiedPlayer) commandSender, "/reply <msg>");
            return;
        }

        UUID target = MeriChat.replyTarget.get(((ProxiedPlayer) commandSender).getUniqueId());
        if (target == null) {
            plugin.promptError((ProxiedPlayer) commandSender, "¡Nadie te ha enviado un mensaje! :(");
            return;
        }

        plugin.processPrivateMessage(target, (ProxiedPlayer) commandSender, strings);

    }
}
