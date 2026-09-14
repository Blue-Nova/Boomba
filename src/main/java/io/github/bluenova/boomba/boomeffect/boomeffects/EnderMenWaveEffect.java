package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import io.github.bluenova.boomba.tools.BoombaTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EnderMenWaveEffect implements BoomEffect {

    final static String log_prefix = "[EnderMenWaveEffect]";
    String id = "endermen_wave";
    boolean active = false;

    int chance_for_wave = ConfigManager.get_enderman_wave_attempt_chance(); // Chance to spawn an enderman each time the task runs, can be later read from config
    int cooldown_seconds = ConfigManager.get_enderman_wave_cooldown_seconds();
    int added_chance = 0;

    BukkitTask bukkitTask;

    Runnable task = () -> {
        //spawn endermen around all players, randomly over a duration of time.
        Boomba.getInstance().log(log_prefix,"Attempting to spawn endermen wave...");
        Random random = new Random();
        if (random.nextInt(100) >= chance_for_wave + added_chance) {
            added_chance += 1; // increase chance for next time, guaranteeing a wave after enough tries
            return;
        }

        // reset added chance once a wave triggers
        added_chance = 0;

        int EndermenToSpawn = 75 * Bukkit.getOnlinePlayers().size(); // can be later read from config
        // every player will have 75 endermen spawned around them.
        // pick players randomly but once one reaches the enderman quota, remove them from the pool.

        // Instead of spawning all at once, schedule spawns on the main thread spaced by 4 ticks (0.2s)
        final int totalToSpawn = Math.max(0, EndermenToSpawn);
        if (totalToSpawn == 0) return;

        Boomba.getInstance().log(log_prefix,"Spawning " + totalToSpawn + " endermen over the next " + (totalToSpawn * 4 / 20.0) + " seconds.");

        UIManager.showEndermenWaveTitle();

        // Use a BukkitRunnable to spawn one enderman every 4 ticks (0.2s)
        new BukkitRunnable() {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned >= totalToSpawn) {
                    this.cancel();
                    return;
                }

                Player player = BoombaTools.pickRandomPlayer();
                if (player == null) {
                    // nothing to spawn for; cancel to avoid infinite running
                    Boomba.getInstance().log(log_prefix,"No players found to spawn endermen for, cancelling remaining spawns.");
                    this.cancel();
                    return;
                }
                spawnEnderman(player);
                spawned++;
                Boomba.getInstance().log(log_prefix,"Spawned enderman " + spawned + "/" + totalToSpawn + " for player " + player.getName());
            }
        }.runTaskTimer(Boomba.getInstance(), 0L, 4L); // 4 ticks = 0.2 seconds
    };

    private void spawnEnderman(Player player) {
        Location spawnLocation = BoombaTools.pickSurfaceBlockInRange(player.getLocation(), 20).getLocation().add(0.5, 1, 0.5);
        player.getWorld().spawnEntity(spawnLocation, EntityType.ENDERMAN);
    }

    @Override
    public void activate() {
        if (active) return;
        active = true;
        // schedule the parent check synchronously; runs every 2 minutes (20 ticks * 60 seconds * 2 minutes = 2400 ticks)
        bukkitTask = Bukkit.getScheduler().runTaskTimer(Boomba.getInstance(), task, 0L, 20L * cooldown_seconds);

        //Boomba.getEventManager().activateEffectListeners(id);
    }

    @Override
    public void deactivate() {
        if (!active) return;
        active = false;
        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        //Boomba.getEventManager().deactivateEffectListeners(id);
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
