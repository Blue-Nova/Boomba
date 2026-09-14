package io.github.bluenova.boomba.game;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class WaveManager {

    // ...existing code...
    private final int WAVE_DURATION_SECONDS = ConfigManager.get_wave_duration_seconds();
    private int current_wave_remaining_time = 0;
    private int current_wave = 0;

    // scheduler task that ticks every second
    private BukkitTask tickTask;
    // if true the tick task will run but will not count down the wave timer
    private volatile boolean paused = false;
    // indicates whether start() has been called and the tick task is scheduled
    private volatile boolean running = false;

    public WaveManager() {
        // default constructor
    }

    // Start the wave manager. If already running this is a no-op.
    public synchronized void start() {
        if (running) return;
        running = true;
        paused = false;
        // initialize timer to backoff before the first wave
        current_wave_remaining_time = WAVE_DURATION_SECONDS;
        // schedule tick every second (20 ticks)
        tickTask = Boomba.getInstance().getServer().getScheduler().runTaskTimer(Boomba.getInstance(), this::tick, 20L, 20L);
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        UIManager.startGame(players);
    }

    // Stop the manager and cancel the scheduled task.
    public synchronized void stop() {
        if (!running) return;
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        running = false;
    }

    // Pause the countdown (task keeps running but won't decrement the timer)
    public void pause() {
        this.paused = true;
    }

    // Resume the countdown
    public void resume() {
        this.paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }

    // The method invoked by the scheduler every second
    private void tick() {
        // hook called every second
        onSecond();

        // if paused, do not count down the wave timer
        if (paused) {
            onPaused();
            return;
        }

        if (current_wave_remaining_time > 0) {
            current_wave_remaining_time--;
            if (current_wave_remaining_time <= 0) {
                // Wave fires
                onWave();
                // reset the cooldown for the next wave
                current_wave_remaining_time = WAVE_DURATION_SECONDS;
            }
        }
    }

    private void onPaused() {

    }

    private void onWave() {
        current_wave++;
        if (current_wave >= Boomba.getBoomEffectsManager().getMaxDifficulty()) {
            // end the game
            stop();
            spawnBoss();
            return;
        }
        Boomba.getBoomEffectsManager().scaleDifficulty(1, true);
        Boomba.getBoomEffectsManager().enableNextEffect();
    }

    private void spawnBoss() {
        Boomba.getBoomEffectsManager().spawnBoss();
    }

    private void onSecond() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        UIManager.updateWave(players, current_wave_remaining_time, WAVE_DURATION_SECONDS);
    }

    // Hooks inherited from WaveManager are empty by default; users can override in a subclass of WaveManagerImpl

    // accessors for the remaining time
    public int getCurrentWaveRemainingTime() {
        return current_wave_remaining_time;
    }

    public void setCurrentWaveRemainingTime(int seconds) {
        if (!running) return; // only allow setting time if the manager is running
        this.current_wave_remaining_time = seconds;
    }

}