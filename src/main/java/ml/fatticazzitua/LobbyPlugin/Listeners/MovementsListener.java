package ml.fatticazzitua.LobbyPlugin.Listeners;

import ml.fatticazzitua.LobbyPlugin.LobbyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementsListener implements Listener {

    private final LobbyPlugin main;

    public MovementsListener(LobbyPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent e) {
        if(this.main.getTeleportsHandler().isPlayerInLobby(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent e) {
        if(this.main.getTeleportsHandler().isPlayerInLobby(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if(this.main.getTeleportsHandler().isPlayerInLobby(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().teleport(JoinListener.getLobbyLocation(this.main)); // per sicurezza
        }
    }

    @EventHandler
    public void onBlockPlacedEvent(BlockPlaceEvent e) {
        if(this.main.getTeleportsHandler().isPlayerInLobby(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
