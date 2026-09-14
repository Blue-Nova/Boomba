package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import java.util.HashMap;

public class ExplosivePunchEffect implements BoomEffect {

    String id = "explosive_punch";
    boolean active = false;

    final double MINIMUM_DAMAGE_THRESHOLD = ConfigManager.get_explosive_punch_minimum_damage_threshold(); // Minimum damage to trigger explosion (adjustable)

    final HashMap<Player, Double> damageTakenMap = new HashMap<>();

    @Override
    public void activate() {
        if (active) return; // Prevent double activation
        Boomba.getEventManager().activateEffectListeners(id); // Activate listeners for this effect
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return; // Prevent double deactivation
        Boomba.getEventManager().deactivateEffectListeners(id); // Deactivate listeners for this effect
        active = false;
    }

    @Override
    public String getInfo() {
        return "Explosive Punch: Damage to you charges up damage for your next punch, which will cause an explosion on hit. The more damage you take, the bigger the explosion.";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void handlePlayerDamage(Player player, double damageTaken) {
        if (!active) return;
        if (player == null) return;
        damageTakenMap.put(player, damageTakenMap.getOrDefault(player, 0.0) + damageTaken);
        Component component = Component.text("§cExplosive Punch Charge: " + String.format("%.1f", damageTakenMap.get(player)) + " damage");
        player.sendActionBar(component);
    }

    public void handlePlayerPunch(Player player, LivingEntity target) {
        if (!active) return;
        if (player == null) return;
        double accumulatedDamage = damageTakenMap.getOrDefault(player, 0.0);
        if (accumulatedDamage > MINIMUM_DAMAGE_THRESHOLD) { // Minimum damage threshold to trigger explosion (adjustable)

            // Create explosion at target location with power based on accumulated damage (capped for balance)
            float explosionPower = (float) (accumulatedDamage / 6.0); // Adjust divisor to balance explosion size
            player.setInvulnerable(true); // Make player temporarily invulnerable to avoid self-damage (optional)
            target.getWorld().createExplosion(target.getLocation(), explosionPower, false, true); // No fire, break blocks

            // throwback target based on explosion power (optional, can be adjusted or removed)
            double throwbackStrength = explosionPower * 3; // Adjust multiplier for throw strength
            target.setVelocity(target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(throwbackStrength).setY(throwbackStrength * 0.3)); // Add upward throw
            target.damage(accumulatedDamage/2);

            player.setInvulnerable(false); // Remove invulnerability after explosion

            damageTakenMap.remove(player); // Reset after punch
        }
    }
}
