package io.github.bluenova.boomba.events.events.FallDamageExplode;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerFallDamageEvent implements Listener {

    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL && player.isSneaking()) {
            float fallDistance = player.getFallDistance();

            // Delegate handling to the effects manager. If the effect handled it (exploded), cancel the damage.
            boolean handled = Boomba.getBoomEffectsManager().handlePlayerFall(player, fallDistance);
            if (handled) {
                event.setCancelled(true);
            }
        }
    }
}
