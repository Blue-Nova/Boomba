package io.github.bluenova.boomba.events.events.UltimateBoomba;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class UltimateBoombaExplodeEvent implements Listener {

    @EventHandler
    public void onUltimateBoombaExplode(EntityExplodeEvent event) {
        if (!Boomba.getBoomEffectsManager().handleUltimateBoombaExplosion(event.getEntity())) return;

        event.blockList().clear();
        event.setYield(0);
        event.setCancelled(true);
    }
}

