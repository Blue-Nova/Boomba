package io.github.bluenova.boomba.events.events.PhantomSuicideBombers;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerKillPhantomEvent implements Listener {

    @EventHandler
    public void onPlayerKillPhantom(EntityDamageByEntityEvent event) {
        // if a player kills a phantom, then drop a random loot item.
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Phantom phantom && (phantom.getHealth() - event.getFinalDamage() <= 0)) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), Boomba.getLootManager().getRandomLoot());
            // 20% chance to drop a second loot item
            if (Math.random() < 0.8) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), Boomba.getLootManager().getRandomLoot());
            }
        }
    }

    @EventHandler
    public void onPlayerKillPhantomWithExplosion(EntityDamageEvent event) {
        // if the phantom dies from an explosion, then drop a spawn egg.
        if (event.getEntity() instanceof Phantom phantom && event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && (phantom.getHealth() - event.getFinalDamage() <= 0)) {
            event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), Boomba.getLootManager().getRandomLoot());
            // 20% chance to drop a second spawn egg
            if (Math.random() < 0.8) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), Boomba.getLootManager().getRandomLoot());
            }
        }
    }

}
