package io.github.bluenova.boomba.events.events.CreepersHighKnockback;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CreeperDamagePlayerEvent implements Listener {

    private double multiplier = ConfigManager.get_creepers_high_knockback_knockback_multiplier();

    @EventHandler
    public void onCreeperDamagePlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.CREEPER && event.getEntity() instanceof Player) {
            // apply force away from creeper to player, with high knockback
            Player player = (Player) event.getEntity();
            player.setVelocity(player.getLocation().toVector().subtract(event.getDamager().getLocation().toVector()).normalize().multiply(multiplier).add(player.getVelocity()));
            Boomba.getInstance().getLogger().info("Player: " + player.getName() + " has velocity: " + player.getVelocity());
        }
    }
}
