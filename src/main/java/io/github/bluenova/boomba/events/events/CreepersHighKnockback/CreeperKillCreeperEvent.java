package io.github.bluenova.boomba.events.events.CreepersHighKnockback;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CreeperKillCreeperEvent implements Listener {

    @EventHandler
    public void onCreeperKillCreeper(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof LivingEntity damager)) return; // Damager must be a living entity (e.g. creeper, player, etc.)
        if (damager.getType() != EntityType.CREEPER) return; // Damager must be a creeper
        if (!(event.getEntity() instanceof LivingEntity reciever)) return; // Damaged entity must be a living entity (e.g. creeper, player, etc.)
        if (reciever.getType() != EntityType.CREEPER) return; // Damaged entity must be a creeper

        if (event.getDamage() >= reciever.getHealth()) {
            // Creepers don't kill other creepers, instead they instantly ignite and explode them.
            event.setCancelled(true);
            reciever.setInvulnerable(true); // Prevent the creeper from taking damage and dying normally
            Creeper creeper = (Creeper) reciever;
            Boomba.getInstance().getLogger().info("A creeper has killed another creeper! Igniting the victim creeper...");
            creeper.explode();
        }

    }

}
