package ml.fatticazzitua.LobbyPlugin;

import ml.fatticazzitua.LobbyPlugin.Listeners.JoinListener;
import ml.fatticazzitua.LobbyPlugin.Listeners.LeaveListener;
import ml.fatticazzitua.LobbyPlugin.Listeners.MovementsListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class LobbyPlugin extends JavaPlugin {

    private TeleportsHandler teleportsHandler;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new MovementsListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
        getLogger().info("[LobbyPlugin] Plugin per la Lobby abilitato - Fluction");
        if(!(new File(getDataFolder(), "config.yml")).exists())
            this.saveDefaultConfig();
        this.teleportsHandler = new TeleportsHandler(this);
        teleportsHandler.serializeCoordinates(false);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        getLogger().info("[LobbyPlugin] Plugin per la Lobby disabilitato.");
        teleportsHandler.serializeCoordinates(true);
        this.saveConfig();
        super.onDisable();
    }

    public TeleportsHandler getTeleportsHandler() {
        return teleportsHandler;
    }
}
