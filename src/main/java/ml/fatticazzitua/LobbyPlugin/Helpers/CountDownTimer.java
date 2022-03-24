package ml.fatticazzitua.LobbyPlugin.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class CountDownTimer extends BukkitRunnable {

    private final Plugin plugin;

    private Integer assignedTaskID;

    private final int timer;
    private int timeLeft;

    private final Runnable afterTimerTask;
    private final Runnable beforeTimerTask;
    Consumer<CountDownTimer> everySecondTask;

    public CountDownTimer(JavaPlugin plugin, int timer, Runnable beforeTimerTask, Runnable afterTimerTask, Consumer<CountDownTimer> everySecondTask) {
        this.plugin = plugin;
        this.timer = timer;
        this.timeLeft = timer;
        this.beforeTimerTask = beforeTimerTask;
        this.afterTimerTask = afterTimerTask;
        this.everySecondTask = everySecondTask;
    }

    @Override
    public void run() {
        if(this.timeLeft < 1) {
            this.afterTimerTask.run();
            if(assignedTaskID != null) Bukkit.getServer().getScheduler().cancelTask(assignedTaskID);
            return;
        }
        if(timeLeft == timer) beforeTimerTask.run();
        everySecondTask.accept(this);
        timeLeft--;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void scheduleTimer() {
        this.assignedTaskID = this.runTaskTimer(plugin, 0L, 20L).getTaskId();
    }
}
