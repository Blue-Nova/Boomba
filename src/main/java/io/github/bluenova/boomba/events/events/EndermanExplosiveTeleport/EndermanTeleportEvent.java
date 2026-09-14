package io.github.bluenova.boomba.events.events.EndermanExplosiveTeleport;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.util.Vector;

public class EndermanTeleportEvent implements Listener {

    private float explosion_power = ConfigManager.get_enderman_explosive_teleport_explosion_power();
    private float explosion_power_per_difficulty = ConfigManager.get_enderman_explosive_teleport_explosion_power_per_difficulty();

    @EventHandler
    public void onEndermanTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof Enderman) {
            // might hurt the Enderman. If so, then explode after the Enderman teleports.
            event.getEntity().setInvulnerable(true);
            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(),
                    explosion_power + (Boomba.getBoomEffectsManager().getCurrentDifficulty() * explosion_power_per_difficulty), false, true);
            event.getEntity().setInvulnerable(false);
            event.getEntity().setVelocity(new Vector().zero()); // Reduce the velocity of the Enderman after teleporting to prevent it from flying away too fast.
        }
    }

}
