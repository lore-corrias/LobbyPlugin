package ml.fatticazzitua.LobbyPlugin.Listeners;

import ml.fatticazzitua.LobbyPlugin.LobbyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class LeaveListener implements Listener {

    private final LobbyPlugin main;

    public LeaveListener(LobbyPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player playerLeaving = e.getPlayer();
        if(this.main.getTeleportsHandler().isCurrentlyInTeleport(playerLeaving)) {
            this.main.getTeleportsHandler().cancelOngoingTeleport(playerLeaving);
            this.main.getLogger().info("Teletrasporto di " + e.getPlayer().getName() + " cancellato.");
        }
        if(Bukkit.getOnlinePlayers().size() == 2) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if(player == playerLeaving)
                    continue;
                if(!this.main.getTeleportsHandler().isCurrentlyInTeleport(player) && !this.main.getTeleportsHandler().isPlayerInLobby(player)) {
                    this.main.getTeleportsHandler().startTeleportToLobby(player);
                }
            }
        }
    }
}
