package es.meriland.chat;

import es.meriland.chat.commands.RCommand;
import es.meriland.chat.commands.TCommand;
import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class MeriChat extends Plugin implements Listener {
    
    public static final HashMap<UUID, UUID> replyTarget = new HashMap<>();
    public static final String MAIN_CHANNEL = "MeriChat";
    public static final String MAIN_SUBCHANNEL = "chat";
    public static final String SINTAXIS = "%prefix%%displayName%%suffix%: ";
    
    private static MeriChat instance;
    private BungeeListener bungeeListener;
    
    @Override
    public void onEnable() {
        instance = this;        
        bungeeListener = new BungeeListener(instance);
        getProxy().getPluginManager().registerCommand(this, new RCommand(this));
        getProxy().getPluginManager().registerCommand(this, new TCommand(this));
        getProxy().registerChannel(MAIN_CHANNEL);
        getProxy().getPluginManager().registerListener(this, bungeeListener);
    }
    
    public static BaseComponent[] parse(String text) {
        return TextComponent.fromLegacyText(text);
    }
}
