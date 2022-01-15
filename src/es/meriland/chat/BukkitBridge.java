package es.meriland.chat;

import es.meriland.chat.listeners.BukkitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, MeriChat.MAIN_CHANNEL);
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
    }
}
