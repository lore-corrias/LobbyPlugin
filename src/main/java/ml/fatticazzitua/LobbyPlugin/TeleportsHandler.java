package ml.fatticazzitua.LobbyPlugin;

import ml.fatticazzitua.LobbyPlugin.Helpers.CountDownTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

public class TeleportsHandler {

    private final LobbyPlugin main;

    private final HashMap<UUID, Location> teleportedPreviousLocations = new LinkedHashMap<>();
    private final HashMap<Player, BukkitRunnable> currentTeleports = new HashMap<>();

    public TeleportsHandler(LobbyPlugin main) {
        this.main = main;
        this.checkTeleportConfigs();
    }

    private void checkTeleportConfigs() {
        if(this.main.getConfig().getInt("teleport-countdown") <= 0) {
            this.main.getConfig().set("teleport-countdown", 100);
        }
        if(this.main.getConfig().getLocation("lobby-location") == null) {
            this.main.getConfig().set("lobby-location", new Location(Bukkit.getServer().getWorld("world"), 0, 250, 0, 0, 0));
        }
    }

    public void startTeleportToLobby(Player player) {
        if(!this.isPlayerInLobby(player)) {
            CountDownTimer timer = new CountDownTimer(this.main, getTeleportTimer(),
                    () -> {
                        teleportedPreviousLocations.put(player.getUniqueId(), player.getLocation());
                        player.sendMessage(ChatColor.RED + "[LobbyPlugin] Sei l'unico nel server, sarai teletrasportato alla lobby.");
                    },
                    () -> this.movePlayerToLobby(player),
                    (t) -> player.sendMessage(ChatColor.RED + "[LobbyPlugin] Teletrasporto tra " + (t.getTimeLeft() % 20 == 0 ? t.getTimeLeft() : t.getTimeLeft() % 20)  + " secondi...")
            );
            currentTeleports.put(player, timer);
            timer.scheduleTimer();
        }
    }

    private void movePlayerToLobby(Player player) {
        if(player.isOnline()) {
            player.sendMessage(ChatColor.RED + "[LobbyPlugin] Teletrasporto in corso...");
            Block underPlayerBlock;
            if((underPlayerBlock = getLobbyLocation().clone().subtract(0, 1, 0).getBlock()).getType().equals(Material.AIR)) {
                this.main.getLogger().warning("Il blocco di teletrasporto per la lobby è inesistente, generandone uno nuovo...");
                underPlayerBlock.setType(Material.STONE);
            }
            player.teleport(getLobbyLocation());
            player.setInvulnerable(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, Integer.MAX_VALUE));
            player.sendTitle(ChatColor.DARK_RED + "grind = ban", ChatColor.RED + "sei cringe + muori + spero ti scopino la madre", 10, 70, 20);
            this.currentTeleports.remove(player);
        } else {
            this.main.getLogger().warning("[LobbyPlugin] Il player " + player.getName() + " non e' online, ma il suo teletrasporto non è stato cancellato");
        }
    }

    public void teleportToPreviousLocation(Player player) {
        if(this.teleportedPreviousLocations.containsKey(player.getUniqueId())) {
            player.teleport(this.teleportedPreviousLocations.get(player.getUniqueId()));
            this.teleportedPreviousLocations.remove(player.getUniqueId());
            player.setInvulnerable(false);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.resetTitle();
        }
    }

    public boolean isCurrentlyInTeleport(Player player) {
        return this.currentTeleports.containsKey(player);
    }

    public void cancelOngoingTeleport(Player player) {
        if(this.currentTeleports.containsKey(player)) {
            this.currentTeleports.get(player).cancel();
            this.currentTeleports.remove(player);
            this.teleportedPreviousLocations.remove(player.getUniqueId());
        }
    }

    public void serializeCoordinates(boolean shutdown) {
        if(shutdown) {
            if(!this.main.getTeleportsHandler().getTeleportedPreviousLocations().isEmpty()) {
                for(String UUID : this.main.getConfig().getConfigurationSection("locations").getKeys(false)) {
                    if(!this.main.getTeleportsHandler().getTeleportedPreviousLocations().containsKey(java.util.UUID.fromString(UUID))) {
                        this.main.getConfig().set("locations." + UUID, null);
                    }
                }
                for(UUID playerUUID : this.main.getTeleportsHandler().getTeleportedPreviousLocations().keySet()) {
                    this.main.getConfig().set("locations." + playerUUID + ".location", this.main.getTeleportsHandler().getTeleportedPreviousLocations().get(playerUUID));
                }
            }
        } else {
            try {
                Set<String> UUIDs = Objects.requireNonNull(this.main.getConfig().getConfigurationSection("locations")).getKeys(false);
                for(String UUID : UUIDs) {
                    this.main.getTeleportsHandler().setTeleportedPreviousLocations(java.util.UUID.fromString(UUID), this.main.getConfig().getLocation("locations." + UUID + ".location"));
                }
            } catch (NullPointerException e) {
                this.main.getLogger().severe(e.getMessage());
            }
        }
    }

    public @Nullable Location getLobbyLocation() {
        return this.main.getConfig().getLocation("lobby-location");
    }

    public @Nullable Integer getTeleportTimer() {
        return this.main.getConfig().getInt("teleport-countdown");
    }

    public boolean isPlayerInLobby(Player player) {
        return this.teleportedPreviousLocations.containsKey(player.getUniqueId()) && !this.currentTeleports.containsKey(player);
    }

    public HashMap<UUID, Location> getTeleportedPreviousLocations() {
        return teleportedPreviousLocations;
    }

    public void setTeleportedPreviousLocations(UUID player, Location location) {
        this.teleportedPreviousLocations.put(player, location);
    }
}
