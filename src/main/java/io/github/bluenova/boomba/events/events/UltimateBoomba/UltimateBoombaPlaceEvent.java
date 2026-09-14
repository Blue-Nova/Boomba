package io.github.bluenova.boomba.events.events.UltimateBoomba;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class UltimateBoombaPlaceEvent implements Listener {

    @EventHandler
    public void onUltimateBoombaPlace(BlockPlaceEvent event) {
        Boomba.getBoomEffectsManager().handleUltimateBoombaPlace(
                event.getPlayer(),
                event.getBlockPlaced(),
                event.getItemInHand()
        );
    }
}

