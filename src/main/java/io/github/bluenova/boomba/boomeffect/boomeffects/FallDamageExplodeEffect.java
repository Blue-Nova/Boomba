package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FallDamageExplodeEffect implements BoomEffect {

    String id = "fall_damage_explode";
    boolean active;

    // Cooldown length in seconds (adjustable)
    private static final long COOLDOWN_SECONDS = ConfigManager.get_fall_damage_explode_cooldown();
    private static final int EXPLOSION_POWER_PER_FALL_DISTANCE =  ConfigManager.get_fall_damage_explode_power_per_fall_distance();
    private static final int EXPLOSION_COUNT_PER_EXPLOSION_POWER= ConfigManager.get_fall_damage_explode_explosion_count_per_explosion_power();

    // Track cooldowns per-player using a thread-safe map of expiry timestamps (epoch millis)
    private final ConcurrentHashMap<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    @Override
    public void activate() {
        if (active) return; // Prevent double activation
        Boomba.getEventManager().activateEffectListeners(id);
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return; // Prevent double deactivation
        Boomba.getEventManager().deactivateEffectListeners(id);
        active = false;
    }

    @Override
    public String getInfo() {
        return "Players cause an explosion on fall damage with a cooldown";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Returns true if the player is currently on cooldown.
     */
    public boolean isPlayerOnCooldown(Player player) {
        if (player == null) return false;
        Long expiry = cooldowns.get(player.getUniqueId());
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public long getRemainingCooldownMillis(Player player) {
        if (player == null) return 0L;
        Long expiry = cooldowns.get(player.getUniqueId());
        if (expiry == null) return 0L;
        long remaining = expiry - System.currentTimeMillis();
        return Math.max(0L, remaining);
    }

    /**
     * Handle the player's fall: returns true if effect triggered (explosion) and false otherwise.
     * This method also applies the cooldown and schedules its removal.
     */
    public boolean handlePlayerFall(Player player, float fallDistance) {
        if (player == null) return false;
        if (fallDistance < 6.0f) return false; // Minimum fall distance to trigger explosion (adjustable)
        UUID uuid = player.getUniqueId();

        // if on cooldown, don't trigger
        if (isPlayerOnCooldown(player)) return false;

        // mark cooldown
        long expiryMillis = System.currentTimeMillis() + (COOLDOWN_SECONDS * 1000L);
        cooldowns.put(uuid, expiryMillis);

        Runnable removeCooldownTask = () -> {
            cooldowns.remove(uuid);
            if (player.isOnline()) {
                player.sendActionBar("§aYour explosive fall is ready again!");
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 7.0f, 2.0f);
            }
        };

        // schedule removal after COOLDOWN_SECONDS (on main thread)
        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), removeCooldownTask, 20L * COOLDOWN_SECONDS);

        // perform the explosion action: schedule explosions spaced by 5 ticks (0.25s)
        player.setInvulnerable(true);
        AtomicReference<Float> explosionPower = new AtomicReference<>(Math.min(70, fallDistance / EXPLOSION_POWER_PER_FALL_DISTANCE)); // 500 explosion power cap for balance (adjust as needed)
        int numberOfExplosions = Math.max(1, (int) Math.ceil(explosionPower.get() / EXPLOSION_COUNT_PER_EXPLOSION_POWER)); // 1 explosion per 10 power, minimum 1

        final long tickDelay = 2L; // 5 ticks = 0.25s
        Location initialLocation = player.getLocation().clone(); // capture initial location to keep explosions centered around it
        for (int i = 0; i < numberOfExplosions; i++) {
            final int idx = i;
            final float currentExplosionPower = explosionPower.get() - (((float) idx /2) * 7); // reduce power for each subsequent explosion
            Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), () -> {
                // ensure player still exists and is valid
                if (!player.isOnline() || !player.isValid()) return;
                float nextExplosionDistance = (idx) + (currentExplosionPower / 10f); // space explosions further apart based on power
                player.getWorld().createExplosion(initialLocation.add(new Vector(0, -nextExplosionDistance, 0)), currentExplosionPower, true);
                float mappedSoundPitch = 1 - Math.max(0.9f, explosionPower.get() / 4f); // Map explosion power to a pitch between 0.1 and 1.0 (higher explosion = lower pitch)
                float mappedSoundVolume = 0.5f + Math.min(3f, explosionPower.get() / 4f); // Map explosion power to a volume between 0.5 and 3.0 (higher explosion = louder)
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, mappedSoundVolume, mappedSoundPitch);
            }, tickDelay * i);
        }

        // schedule re-enabling vulnerability after last explosion
        long totalDelayTicks = tickDelay * numberOfExplosions * 5;
        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), () -> {
            if (player.isOnline() && player.isValid()) {
                player.setInvulnerable(false);
            }
        }, totalDelayTicks);

        return true;
    }

}
