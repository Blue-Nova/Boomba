package io.github.bluenova.boomba.events.events.ExplosivePunch;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamagedEvent implements Listener {

     @EventHandler
     public void onPlayerDamaged(EntityDamageEvent event) {
         if (!(event.getEntity() instanceof Player player)) return; // Only trigger on players
         // if damage is fall damage, return
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) return;
         double damage = Math.min(event.getFinalDamage(), 40); // cap damage to prevent excessive explosions
         Boomba.getBoomEffectsManager().handleExplosivePunchPlayerDamaged(player, damage);
     }
}
