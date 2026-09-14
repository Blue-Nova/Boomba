package io.github.bluenova.boomba.events.events.Bastion;

import io.github.bluenova.boomba.boomeffect.boomeffects.BastionEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.entity.Wither;

public class BreakBastionEvent implements Listener {

    private boolean isProtectedBastionBeacon(Block block) {
        if (block.getType() != Material.BEACON) return false;

        BlockState state = block.getState();
        if (!(state instanceof TileState tileState)) return false;

        return BastionEffect.isBastionBlock(tileState.getPersistentDataContainer());
    }

    @EventHandler
    public void onBreakBastion(BlockBreakEvent event) {
        if (isProtectedBastionBeacon(event.getBlock())) {
            // This block has the Bastion marker, so prevent it from being broken
            event.setCancelled(true);
            // Optionally notify the player
            if (event.getPlayer() != null) {
                event.getPlayer().sendMessage("This Bastion beacon is protected and cannot be broken.");
            }
        }
    }

    @EventHandler
    public void onWitherExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Wither)) return;
        event.blockList().removeIf(this::isProtectedBastionBeacon);
    }

    @EventHandler
    public void onWitherChangeBlock(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof Wither)) return;
        if (!isProtectedBastionBeacon(event.getBlock())) return;
        event.setCancelled(true);
    }

}
