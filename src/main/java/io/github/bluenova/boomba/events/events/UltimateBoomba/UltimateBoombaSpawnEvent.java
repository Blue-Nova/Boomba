package io.github.bluenova.boomba.events.events.UltimateBoomba;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class UltimateBoombaSpawnEvent implements Listener {

    @EventHandler
    public void onUltimateBoombaPrimed(EntitySpawnEvent event) {
        Boomba.getBoomEffectsManager().handleUltimateBoombaPrimedEntitySpawn(event.getEntity());
    }
}

