package io.github.bluenova.boomba.events.events.ExplosivePunch;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerPunchEntityEvent implements Listener {

    @EventHandler
    public void onPlayerPunchEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            player.getInventory().getItemInMainHand();
            // only punch with empty hand. If holding an item, then don't explode.
            if (!player.getInventory().getItemInMainHand().getType().isAir()) return;
            double damage = event.getFinalDamage();
            if (damage > 0) {
                Boomba.getBoomEffectsManager().handleExplosivePunchPlayerPunch(player, (LivingEntity) event.getEntity());
            }
        }
    }

}
