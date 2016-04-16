package es.meriland.chat;

import es.meriland.chat.commands.TCommand;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class MeriChat extends Plugin implements Listener {
    
    public static final String MAIN_CHANNEL = "MeriChat";
    public static final String MAIN_SUBCHANNEL = "chat";
    
    private static MeriChat instance;
    private BungeeListener bungeeListener;
    
    @Override
    public void onEnable() {
        instance = this;        
        bungeeListener = new BungeeListener(instance);
        
        getProxy().getPluginManager().registerCommand(this, new TCommand(this));
        getProxy().registerChannel(MAIN_CHANNEL);
        getProxy().getPluginManager().registerListener(this, bungeeListener);
    }
}
