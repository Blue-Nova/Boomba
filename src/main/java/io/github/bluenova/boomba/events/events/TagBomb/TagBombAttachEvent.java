package io.github.bluenova.boomba.events.events.TagBomb;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TagBombAttachEvent implements Listener {

    @EventHandler
    public void onTagBombAttach(PlayerInteractEntityEvent event) {

        if (event.getRightClicked().getType().isAlive()) {
            Boomba.getBoomEffectsManager().handleTagBombAttach(event.getPlayer(), event.getRightClicked());
        }
    }
}
