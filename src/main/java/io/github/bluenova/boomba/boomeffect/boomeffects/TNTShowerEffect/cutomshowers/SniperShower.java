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
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

/**
 * SniperShower
 * ----------------
 * Spawns several TNTs high above a target player at randomized horizontal offsets.
 * After a short delay (allowing the TNT to appear in the air), each TNT is given
 * a velocity toward the player's current position so it "snipes" the player.
 *
 * Important design decisions (non-blocking / scheduler-based):
 * - Spawns and velocity application are done on the main thread using Bukkit's scheduler
 *   (no Thread.Sleep or blocking allowed on main thread).
 * - Each TNT receives a long initial fuse to avoid exploding before it is launched.
 * - After velocity is applied we reset the TNT fuse to match the expected travel time
 *   + a safety margin so it explodes shortly after reaching the target.
 */
public class SniperShower extends CustomShowerClass implements CustomShower {

    // -------------------- Configuration --------------------
    // How many TNTs will be spawned in a single shower
    private final int totalSpawns = ConfigManager.get_tnt_shower_sniper_shower_total_spawns(); // 4

    // Delay between subsequent spawns of TNT in ticks (20 ticks = 1 second).
    // Here it's 20L*6 = 120 ticks = 6 seconds between spawns.
    private final long intervalTicks = ConfigManager.get_tnt_shower_sniper_shower_interval_ticks(); // 20L*5

    // Vertical offset above the player's feet/position where TNT will be spawned
    // (so TNT appears high in the air and can be "shot" toward the player)
    private final int heightAbovePlayer = ConfigManager.get_tnt_shower_sniper_shower_height_above_player(); // 20;

    // Horizontal random radius (in blocks) used to pick an X/Z offset around player
    // The spawned TNT will be placed at player location + random(-radius..radius) for X and Z
    private final int radius = ConfigManager.get_tnt_shower_sniper_shower_radius(); // 30;

    // -------------------- Launch behaviour --------------------
    // How long to wait (in ticks) AFTER spawning the TNT before applying its velocity.
    // This gives the TNT time to exist in the air and avoids instantaneous movement at spawn.
    private final long delayBeforeLaunchTicks = ConfigManager.get_tnt_shower_sniper_shower_delay_before_launch_ticks(); // 30L;

    // Desired speed for the TNT after launch expressed in blocks-per-tick.
    // travel_time_ticks = distance_in_blocks / speedPerTick
    private final double speedPerTick = ConfigManager.get_tnt_shower_sniper_shower_speed_per_tick(); // 4 // 1 block per tick -> 20 blocks / second

    // Safety margin (in ticks) to add to the fuse so TNT will explode shortly after arriving
    // at the target. Accounts for minor delays, collisions and travel variance.
    private final int explosionMarginTicks = ConfigManager.get_tnt_shower_sniper_shower_explosion_margin_ticks(); // 2;

    // The repeating task that spawns TNTs; stored so we can cancel early in stopShower()
    private BukkitRunnable task;

    @Override
    public void startShower(Player player, Runnable onComplete) {
        // Guard: if player is invalid or offline, call onComplete immediately (if provided)
        if (player == null || !player.isOnline()) {
            if (onComplete != null) {
                Boomba.getInstance().getServer().getScheduler().runTask(Boomba.getInstance(), onComplete);
            }
            return;
        }

        // Create a repeating BukkitRunnable that will spawn `totalSpawns` TNTs with `intervalTicks` delay
        task = new BukkitRunnable() {
            int spawned = 0; // local counter of how many TNTs we've spawned so far

            @Override
            public void run() {
                // If the target went offline while the shower is running, stop and call onComplete
                if (!player.isOnline()) {
                    cancelAndFinish();
                    return;
                }

                // If we've spawned the configured amount, stop and call onComplete
                if (spawned >= totalSpawns + (Boomba.getBoomEffectsManager().getCurrentDifficulty() / 6)) { // increase spawns as difficulty increases
                    cancelAndFinish();
                    return;
                }

                spawned++;

                // Spawn a single sniper-style TNT for this iteration
                spawnSniperTNT(player);
            }

            private void cancelAndFinish() {
                cancel();
                if (onComplete != null) {
                    // Ensure onComplete runs on main thread (safety)
                    Boomba.getInstance().getServer().getScheduler().runTask(Boomba.getInstance(), onComplete);
                }
            }
        };

        // Start the repeating spawn task immediately and then every `intervalTicks` ticks
        task.runTaskTimer(Boomba.getInstance(), 0L, intervalTicks - (Boomba.getBoomEffectsManager().getCurrentDifficulty() * 6L)); // decrease interval as difficulty increases
    }

    /**
     * Spawn a single TNT above the player, and schedule it to be launched toward the player's
     * current position after a short delay. The function also schedules `onComplete` slightly after
     * the last TNT is expected to explode.
     *
     * Important math and scheduling steps:
     *  - spawnLoc = player location + random X/Z offsets + heightAbovePlayer
     *  - delayBeforeLaunchTicks passes, then we sample player's position again (target)
     *  - dir = target - tnt.location (vector pointing from TNT to target)
     *  - distance = dir.length() (euclidean distance in blocks)
     *  - velocity = dir.normalized() * speedPerTick  (makes TNT move `speedPerTick` blocks/tick toward target)
     *  - travelTicks = ceil(distance / speedPerTick)
     *  - fuseTicks = delayBeforeLaunchTicks + travelTicks + explosionMarginTicks
     */
    private void spawnSniperTNT(Player player) {
        // Base player location (can include fractional coordinates)
        Location base = player.getLocation();

        // Choose a random horizontal offset in [-radius, radius) for both X and Z
        double offsetX = ThreadLocalRandom.current().nextDouble(-radius, radius);
        double offsetZ = ThreadLocalRandom.current().nextDouble(-radius, radius);

        // Final spawn location: player's location plus the random horizontal offset and a vertical offset
        Location spawnLoc = base.clone().add(offsetX, heightAbovePlayer, offsetZ);

        // Spawn TNTPrimed at the calculated location
        TNTPrimed tnt = player.getWorld().spawn(spawnLoc, TNTPrimed.class);

        // NOTE: in this implementation gravity is disabled initially to avoid the TNT immediately
        // falling before we get a chance to apply an intentional velocity. This is a design choice
        // — you may prefer to enable gravity and let natural physics handle the arc.
        tnt.setGravity(false); // disable gravity until we apply the launch velocity

        // Give a large initial fuse so the TNT won't explode before we launch it.
        // We add a base value (200 ticks) plus the delayBeforeLaunchTicks so there's ample time.
        tnt.setFuseTicks((int) (delayBeforeLaunchTicks + 200));

        player.playSound(spawnLoc, Sound.ENTITY_TNT_PRIMED, 6f, 1.0f);

        // We capture the player's location at the time of spawning to use as the initial target.
        Location target = player.getLocation().clone();
        // Schedule launch on the main thread after delayBeforeLaunchTicks ticks
        Boomba.getInstance().getServer().getScheduler().runTaskLater(Boomba.getInstance(), () -> {
            // Safety checks: make sure the entity and player are still valid
            if (tnt.isDead() || !player.isOnline()) return;

            // Direction vector from the TNT to the player target
            Vector dir = target.toVector().subtract(tnt.getLocation().toVector());

            // Straight-line distance (in blocks). This is the magnitude of `dir`.
            double distance = dir.length();

            // Avoid division by zero and tiny distances by clamping
            if (distance < 0.001) distance = 0.1;

            // Normalize the direction vector (unit length) then multiply by speedPerTick to get
            // the desired velocity vector in blocks-per-tick. This makes TNT move toward the target
            // at `speedPerTick` blocks per tick.
            Vector velocity = dir.normalize().multiply(speedPerTick);

            // Apply the computed velocity to the TNT entity
            tnt.setVelocity(velocity);

            // (Optional) Log details for debugging: spawn location, target, velocity and distance
            Boomba.getInstance().getLogger().info("SniperShower: spawned tnt at " + spawnLoc + " targeting " + target + " velocity=" + velocity + " distance=" + distance);

            // Estimate travel time in ticks = ceil(distance / speedPerTick)
            int travelTicks = (int) Math.ceil(distance / speedPerTick);

            // Compute fuse so TNT explodes after launchDelay + travel time + margin
            // We include delayBeforeLaunchTicks because fuse is measured in ticks from now but we originally
            // set fuse longer to avoid early explosion; here we reset it to the expected remaining lifetime.
            int fuseTicks = travelTicks + explosionMarginTicks;
            Boomba.getInstance().getLogger().info("SniperShower: calculated fuseTicks=" + fuseTicks + " (travelTicks=" + travelTicks + ")");
            // Safety: ensure fuse is not ridiculously small
            if (fuseTicks < 10) fuseTicks = 10;

            // Apply the new fuse value so the TNT will detonate shortly after arrival
            tnt.setFuseTicks(fuseTicks);
        }, delayBeforeLaunchTicks - (long)(Boomba.getBoomEffectsManager().getCurrentDifficulty() / 1.5)); // decrease delay before launch as difficulty increases
    }

    @Override
    public void stopShower() {
        // Cancel the repeating spawn task if it is running
        if (task != null) task.cancel();
    }
}