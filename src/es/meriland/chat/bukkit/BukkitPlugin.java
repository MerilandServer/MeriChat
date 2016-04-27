package es.meriland.chat.bukkit;

import es.meriland.chat.MeriChat;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    private BukkitListener bukkitListener;
    
    public static Permission permission = null;
    public static Chat chat = null;
    
    @Override
    public void onEnable() {
        bukkitListener = new BukkitListener(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, MeriChat.MAIN_CHANNEL);
        getServer().getPluginManager().registerEvents(bukkitListener, this);

        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            setupVault();
        }
    }
    
    private void setupVault() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
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