package io.github.bluenova.boomba.events.events.AllExplodeOnDeath;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.Vector;

public class PlayerDamageExplosionEvent implements Listener {

    private static final double EPSILON_SQ = 1e-6;

    private static boolean isFinite(Vector v) {
        return Double.isFinite(v.getX()) && Double.isFinite(v.getY()) && Double.isFinite(v.getZ());
    }

    private static Vector safeNormalize(Vector v) {
        double lenSq = v.lengthSquared();
        if (lenSq <= EPSILON_SQ) return null;
        double invLen = 1.0 / Math.sqrt(lenSq);
        return v.clone().multiply(invLen);
    }

    // make any blockexplosion damage blockable with the shield
    @EventHandler
    public void onPlayerDamageExplosion(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            // if the player is blocking with a shield, then cancel the explosion damage
            Boomba.getInstance().getLogger().info("Player damage explosion event detected. Player is blocking: " + player.isBlocking());
            if (player.isBlocking()) {
                // use eye direction for more accurate facing
                Vector rawPlayerDirection = player.getEyeLocation().getDirection();
                if (!isFinite(rawPlayerDirection)) {
                    Boomba.getInstance().getLogger().warning("Player eye direction contains non-finite components: " + rawPlayerDirection);
                    return;
                }
                Vector playerDirection = safeNormalize(rawPlayerDirection);
                if (playerDirection == null) {
                    Boomba.getInstance().getLogger().warning("Player eye direction is too small to normalize. Aborting direction check.");
                    return;
                }

                // attempt to compute vector from player to the damage source; if zero-length, try to look up recent explosion locations
                Location playerLoc = player.getLocation();
                Vector sourceVector = event.getEntity().getLocation().toVector().subtract(playerLoc.toVector());

                if (!isFinite(sourceVector) || sourceVector.lengthSquared() <= EPSILON_SQ) {
                    // try to find a recent explosion near the player
                    Location explosion = ExplosionTracker.getNearestRecent(playerLoc, 8.0);
                    if (explosion != null) {
                        sourceVector = explosion.toVector().subtract(playerLoc.toVector());
                        Boomba.getInstance().getLogger().info("Found recent explosion at " + explosion + " for direction calculation.");
                    } else {
                        Boomba.getInstance().getLogger().warning("Could not determine explosion source direction (zero-length or non-finite vector) and no recent explosion tracked. Skipping direction check.");
                        return; // cannot determine direction safely, don't try to cancel based on NaN
                    }
                }

                Boomba.getInstance().getLogger().info("Player direction: " + playerDirection);
                Boomba.getInstance().getLogger().info("Raw source vector: " + sourceVector);

                if (!isFinite(sourceVector)) {
                    Boomba.getInstance().getLogger().warning("Source vector contains non-finite components after lookup. Aborting.");
                    return;
                }

                Vector normSource = safeNormalize(sourceVector);
                if (normSource == null) {
                    Boomba.getInstance().getLogger().warning("Source vector too small to normalize after lookup. Aborting.");
                    return;
                }

                double condition = playerDirection.dot(normSource);
                Boomba.getInstance().getLogger().info("Condition value: " + condition);
                if (Double.isNaN(condition)) {
                    Boomba.getInstance().getLogger().warning("Direction dot product resulted in NaN even after checks. Aborting.");
                    return;
                }
                if (condition > 0) {
                    event.setCancelled(true);

                    // find which hand holds the shield (either offhand or main hand)
                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    ItemStack mainHand = player.getInventory().getItemInMainHand();
                    ItemStack shield;

                    if (offHand.getType() == Material.SHIELD) {
                        shield = offHand;
                    } else if (mainHand.getType() == Material.SHIELD) {
                        shield = mainHand;
                    } else {
                        Boomba.getInstance().getLogger().warning("Player is blocking with a shield, but not holding it in either hand. Cancelling explosion damage anyway.");
                        return;
                    }

                    // apply durability damage to the shield via its ItemMeta (Damageable)
                    if (shield.hasItemMeta()) {
                        org.bukkit.inventory.meta.ItemMeta meta = shield.getItemMeta();
                        if (meta instanceof Damageable damageableMeta) {
                            int damageToApply = (int) Math.ceil(event.getFinalDamage());
                            int newDamage = damageableMeta.getDamage() + damageToApply;
                            damageableMeta.setDamage(newDamage);
                            shield.setItemMeta(damageableMeta);
                        } else {
                            Boomba.getInstance().getLogger().warning("Shield item meta is not Damageable; cannot apply durability damage.");
                        }
                    }
                }
            }
        }
    }
}
