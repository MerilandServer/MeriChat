package es.meriland.chat.bukkit;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin implements Listener {

    private BukkitListener bukkitListener;
    
    public static Permission permission = null;
    public static Chat chat = null;
    
    @Override
    public void onEnable() {

        bukkitListener = new BukkitListener(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "MeriChat");
        getServer().getMessenger().registerIncomingPluginChannel(this, "MeriChat", bukkitListener);
        getServer().getPluginManager().registerEvents(this, this);

        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            setupPermissions();
            setupChat();
        }
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.
                getServer().getServicesManager().getRegistration(
                        net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().
                getServicesManager().getRegistration(
                        net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }
    
    public String getGroup(Player player){
        try {
            if (permission != null && permission.getPrimaryGroup(player) != null) {
                return permission.getPrimaryGroup(player);
            }
        } catch (UnsupportedOperationException ignored){
        }
        return "Miembro";
    }

    public String getPrefix(Player player){
        if(chat != null){
            return chat.getPlayerPrefix(player);
        }
        return "";
    }

    public String getSuffix(Player player){
        if(chat != null){
            return chat.getPlayerSuffix(player);
        }
        return "";
    }
}