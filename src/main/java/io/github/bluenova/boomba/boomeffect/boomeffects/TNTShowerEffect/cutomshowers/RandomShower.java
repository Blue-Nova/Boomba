package io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.cutomshowers;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.CustomShower;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.CustomShowerClass;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class RandomShower extends CustomShowerClass implements CustomShower {

    private final int totalSpawns = ConfigManager.get_tnt_shower_random_shower_total_spawn(); // 12 number of TNT to spawn
    private final long intervalTicks = ConfigManager.get_tnt_shower_random_shower_interval_ticks(); // 12 ticks between spawns (20 ticks = 1s)
    private final int heightAbovePlayer = ConfigManager.get_tnt_shower_random_shower_height_above_player(); // 12 spawn height above player
    private final int radius = ConfigManager.get_tnt_shower_random_shower_raduis(); // 10 block radius around player for random spawns
    private final int TNT_FUSE_TICK = ConfigManager.get_tnt_shower_random_shower_fuse_ticks(); // 80 ticks = 4 seconds until explosion

     public RandomShower() {
     }

    @Override
    public void startShower(Player player, Runnable onComplete) {
        if (player == null || !player.isOnline()) {
            if (onComplete != null) {
                Boomba.getInstance().getLogger().info("THIS SHOULD NOT FIRE");
                Boomba.getInstance().getServer().getScheduler().runTask(Boomba.getInstance(), onComplete);
            }
            return;
        }

        new BukkitRunnable() {
            int spawned = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelAndFinish();
                    return;
                }

                if (spawned >= totalSpawns + (Boomba.getBoomEffectsManager().getCurrentDifficulty())) { // increase spawns as difficulty increases
                    cancelAndFinish();
                    return;
                }

                spawned++;
                spawnRandomTNTAbove(player);
            }

            private void cancelAndFinish() {
                cancel();
                if (onComplete != null) {
                    Boomba.getInstance().getServer().getScheduler().runTaskLater(
                            Boomba.getInstance(), onComplete, TNT_FUSE_TICK); // slight delay to ensure all TNT has spawned before next shower starts
                }
            }
        }.runTaskTimer(Boomba.getInstance(), 0L, (long)(intervalTicks - (Boomba.getBoomEffectsManager().getCurrentDifficulty() / 4.0))); // decrease interval as difficulty increases
    }

    private void spawnRandomTNTAbove(Player player) {
        Location base = player.getLocation();
        double scaledRadius = radius + (Boomba.getBoomEffectsManager().getCurrentDifficulty() / 4.0); // increase radius as difficulty increases
        double offsetX = ThreadLocalRandom.current().nextDouble(-scaledRadius, scaledRadius);
        double offsetZ = ThreadLocalRandom.current().nextDouble(-scaledRadius, scaledRadius);
        Location spawnLoc = base.clone().add(offsetX, heightAbovePlayer, offsetZ);

        TNTPrimed tnt = player.getWorld().spawn(spawnLoc, TNTPrimed.class);
        tnt.setFuseTicks(Math.max(10, TNT_FUSE_TICK - (Boomba.getBoomEffectsManager().getCurrentDifficulty()))); // 2 seconds until explosion (adjust if needed)
        // optionally adjust velocity, etc.
    }
}
