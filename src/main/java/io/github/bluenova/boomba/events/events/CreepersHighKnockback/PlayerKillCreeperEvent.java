package io.github.bluenova.boomba.events.events.CreepersHighKnockback;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerKillCreeperEvent implements Listener {

    @EventHandler
    public void onPlayerKillCreeper(EntityDamageByEntityEvent event) {
        // if the player kills the creeper, then explode the creeper.
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Creeper creeper && creeper.getHealth() - event.getFinalDamage() <= 0) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(Material.CREEPER_SPAWN_EGG, 1));
        }
    }
}
