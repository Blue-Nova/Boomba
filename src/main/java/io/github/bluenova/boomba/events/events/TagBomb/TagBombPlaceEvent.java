package io.github.bluenova.boomba.events.events.TagBomb;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TagBombPlaceEvent implements Listener {

    @EventHandler
    public void onTagBombPlace(BlockPlaceEvent event) {
        // hand the placement to the effect; it verifies the item's PersistentDataContainer tag
        Boomba.getBoomEffectsManager().handleTagBombPlace(
                event.getPlayer(),
                event.getBlockPlaced(),
                event.getItemInHand()
        );
    }

    @EventHandler
    public void onTagBombExplode(EntityExplodeEvent event) {
        // if the exploding entity is a tracked Tag Bomb (PDC tag), cancel the vanilla explosion
        // and let the effect run its custom no-damage launch explosion instead
        if (Boomba.getBoomEffectsManager().handleTagBombExplosion(event.getEntity())) {
            Boomba.getInstance().getLogger().info("Cancelled vanilla explosion for Tag Bomb with custom explosion effect");
            event.setCancelled(true);
        }
    }
}

