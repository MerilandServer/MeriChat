package es.meriland.chat;

import java.util.HashMap;
import java.util.UUID;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class MeriChat extends Plugin implements Listener {

    public static final String MAIN_CHANNEL = "MeriChat";
    public static final String MAIN_SUBCHANNEL = "chat";
    public static final String PRIVATE_SUBCHANNEL = "priv";
    public static final String SINTAXIS = "%prefix%%displayName%%suffix%: ";
    public static final String CHAR = "\u00A8";
    
    public final HashMap<UUID, UUID> chatsActivados = new HashMap<>();
    public final HashMap<UUID, UUID> replyTarget = new HashMap<>();
    
    private static MeriChat instance;
    private BungeeListener bungeeListener;
    
    @Override
    public void onEnable() {
        instance = this;        
        bungeeListener = new BungeeListener(instance);
        getProxy().registerChannel(MAIN_CHANNEL);
        getProxy().getPluginManager().registerListener(this, bungeeListener);
    }
    
    public void setReply(UUID user, UUID replyTo) {
        if (replyTarget.containsKey(user)) replyTarget.remove(user);
        
        replyTarget.put(user, replyTo);
    }
}
