package es.meriland.chat;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class MeriChat extends Plugin implements Listener {
    
    private static MeriChat instance;
    private BungeeListener bungeeListener;
    
    @Override
    public void onEnable() {
        instance = this;        
        bungeeListener = new BungeeListener(instance);
        
        getProxy().registerChannel("MeriChat");
        getProxy().getPluginManager().registerListener(this, bungeeListener);
    }
}
