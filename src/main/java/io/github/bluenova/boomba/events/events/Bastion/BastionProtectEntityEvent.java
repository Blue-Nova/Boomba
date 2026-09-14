package io.github.bluenova.boomba.events.events.Bastion;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.boomeffects.BastionEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class BastionProtectEntityEvent implements Listener {

    private final int percentage_explosion_damage_reduction_in_range = ConfigManager.get_bastion_explosion_damage_reduction_in_range();

    // Check whether there's any bastion block within a spherical radius of the provided location
    private boolean isNearBastion(Location loc) {
        World world = loc.getWorld();
        if (world == null) return false;

        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        int r = Boomba.getBoomEffectsManager().getBastionRange();
        int r2 = r * r;

        for (int dx = -r; dx <= r; dx++) {
            int dx2 = dx * dx;
            for (int dy = -r; dy <= r; dy++) {
                int dy2 = dy * dy;
                for (int dz = -r; dz <= r; dz++) {
                    int dist2 = dx2 + dy2 + dz * dz;
                    if (dist2 > r2) continue;

                    int x = cx + dx;
                    int y = cy + dy;
                    int z = cz + dz;

                    // quick y bounds check
                    if (y < 0 || y > world.getMaxHeight()) continue;

                    Block check = world.getBlockAt(x, y, z);
                    BlockState state = check.getState();

                    if (state instanceof TileState tile) {
                        if (BastionEffect.isBastionBlock(tile.getPersistentDataContainer())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        DamageCause cause = event.getCause();
        if (cause != DamageCause.ENTITY_EXPLOSION && cause != DamageCause.BLOCK_EXPLOSION) return;

        Entity entity = event.getEntity();

        if (isNearBastion(entity.getLocation())) {
            if (entity instanceof Player) {
                // Players take half explosion damage inside bastion range
                event.setDamage((event.getDamage()) - event.getDamage() * ((double) Math.min(percentage_explosion_damage_reduction_in_range,100) /100));
            } else {
                // Other entities take no explosion damage
                event.setCancelled(true);
            }
        }
    }
}
