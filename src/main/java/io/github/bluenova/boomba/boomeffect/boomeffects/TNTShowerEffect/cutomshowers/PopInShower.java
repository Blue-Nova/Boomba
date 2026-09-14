package io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.cutomshowers;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.CustomShower;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.CustomShowerClass;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

public class PopInShower extends CustomShowerClass implements CustomShower {

    private final int totalSpawns = ConfigManager.get_tnt_shower_popin_shower_total_spawns();
    private final long intervalTicks = ConfigManager.get_tnt_shower_popin_shower_interval_ticks(); // 6 seconds between spawns
    private final int tnt_fuse_ticks = ConfigManager.get_tnt_shower_popin_shower_fuse_ticks(); // 2.25 seconds until explosion
    private final int seated_fuse_ticks = ConfigManager.get_tnt_shower_popin_seated_fuse_ticks(); // 3 seconds until explosion

     public PopInShower() {
     }

     @Override
     public void startShower(Player player, Runnable onComplete) {
         // this shower will spawn TNT directly on the player's location, giving a "pop-in" effect. It will spawn 5 TNT with a short delay between each spawn.


            new BukkitRunnable() {
                int spawned = 0;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancelAndFinish();
                        return;
                    }

                    if (spawned >= totalSpawns) {
                        cancelAndFinish();
                        return;
                    }

                    spawned++;
                    spawnTNTOnPlayer(player);
                }

                private void cancelAndFinish() {
                    cancel();
                    if (onComplete != null) {
                        Boomba.getInstance().getServer().getScheduler().runTaskLater(
                                Boomba.getInstance(), onComplete, 50L); // slight delay to ensure all TNT has spawned before next shower starts
                    }
                }
            }.runTaskTimer(Boomba.getInstance(), 0L, intervalTicks - (Boomba.getBoomEffectsManager().getCurrentDifficulty() * 2L)); // decrease interval as difficulty increases
     }

    private void spawnTNTOnPlayer(Player player) {
        if (player == null || !player.isOnline()) return;

        Location loc = player.getLocation();
        loc.setY(loc.getY() + 1); // spawn slightly above the player's head to ensure they "pop in" with the TNT
        TNTPrimed tnt = player.getWorld().spawn(loc, TNTPrimed.class);
        tnt.setFuseTicks(tnt_fuse_ticks - (Boomba.getBoomEffectsManager().getCurrentDifficulty()/2)); // 2.25 seconds until explosion
        // 20% to add the player as a passenger of the TNT, so they "pop in" with it
        if (Math.random() < 0.2) {
            tnt.addPassenger(player); // make the player a passenger of the TNT so they "pop in" with it
            player.playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1.0f, 0.8f); // play a sound effect when the player is "picked up" by the TNT
            tnt.setFuseTicks(seated_fuse_ticks - (Boomba.getBoomEffectsManager().getCurrentDifficulty()/2)); // if the player is seated, give them a slightly longer fuse to allow for the visual effect
        }
     }

     @Override
     public void stopShower() {
    }
}
