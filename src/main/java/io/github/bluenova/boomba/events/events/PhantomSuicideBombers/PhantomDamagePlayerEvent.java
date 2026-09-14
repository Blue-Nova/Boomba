package io.github.bluenova.boomba.events.events.PhantomSuicideBombers;

import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PhantomDamagePlayerEvent implements Listener {

    private float explosion_power = ConfigManager.get_phantom_suicide_bombers_explosion_power();

    @EventHandler
    public void onPhantomDamagePlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PHANTOM && event.getEntity() instanceof Player) {
            if (event.getFinalDamage() <= 0) {
                return;
            }
            event.setCancelled(true);
            // apply force away from phantom to player, with high knockback
            if (event.getDamager() instanceof LivingEntity) {
                LivingEntity damager = (LivingEntity) event.getDamager();
                Location damagerLocation = damager.getLocation();
                World world = damager.getWorld();
                world.createExplosion(damagerLocation, explosion_power, false, true);
                damager.setHealth(0);
            }
        }
    }
}
