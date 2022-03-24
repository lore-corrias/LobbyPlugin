package ml.fatticazzitua.LobbyPlugin.Listeners;

import ml.fatticazzitua.LobbyPlugin.LobbyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinListener implements Listener {

    private final LobbyPlugin main;

    public JoinListener(LobbyPlugin main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(Bukkit.getOnlinePlayers().size() == 1) {
            this.main.getTeleportsHandler().startTeleportToLobby(e.getPlayer());
        } else if(Bukkit.getOnlinePlayers().size() > 1) {
            for (Player playerToBeTeleported : Bukkit.getOnlinePlayers()) {
                if(!this.main.getTeleportsHandler().getTeleportedPreviousLocations().containsKey(playerToBeTeleported.getUniqueId()))
                    continue;
                if (this.main.getTeleportsHandler().isCurrentlyInTeleport(playerToBeTeleported)) {
                    this.main.getTeleportsHandler().cancelOngoingTeleport(playerToBeTeleported);
                    playerToBeTeleported.sendMessage(ChatColor.RED + "[LobbyPlugin] Teletrasporto cancellato.");
                } else {
                    playerToBeTeleported.sendMessage(ChatColor.RED + "[LobbyPlugin] Un nuovo player si è unito e sei stato teletrasportato nella tua posizione precedente.");
                    this.main.getTeleportsHandler().teleportToPreviousLocation(playerToBeTeleported);
                }
            }
        }
    }

    public static Location getLobbyLocation(LobbyPlugin main) {
        return main.getConfig().getObject("lobby-location", Location.class);
    }
}