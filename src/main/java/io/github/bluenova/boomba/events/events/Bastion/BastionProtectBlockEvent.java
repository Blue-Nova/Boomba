package io.github.bluenova.boomba.events.events.Bastion;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.boomeffects.BastionEffect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;
import java.util.List;

public class BastionProtectBlockEvent implements Listener {

    // Helper to determine whether a given block state holds a bastion marker
    private boolean isTileStateBastion(BlockState state, BastionEffect checker) {
        if (state instanceof TileState tile) {
            return checker.isBastionBlock(tile.getPersistentDataContainer());
        }
        return false;
    }

    // Remove protected blocks from a list of blocks scheduled to be exploded
    private void protectBlocksFromExplosion(List<Block> blocks, BastionEffect checker) {
        Iterator<Block> it = blocks.iterator();
        while (it.hasNext()) {
            Block candidate = it.next();
            boolean protectedByBastion = false;
            int r = Boomba.getBoomEffectsManager().getBastionRange();
            int r2 = r * r;

            outer:
            for (int dx = -r; dx <= r; dx++) {
                int dx2 = dx * dx;
                for (int dy = -r; dy <= r; dy++) {
                    int dy2 = dy * dy;
                    for (int dz = -r; dz <= r; dz++) {
                        int dist2 = dx2 + dy2 + dz * dz;
                        if (dist2 > r2) continue; // keep spherical/round radius

                        Block check = candidate.getWorld().getBlockAt(candidate.getX() + dx, candidate.getY() + dy, candidate.getZ() + dz);
                        BlockState state = check.getState();
                        if (isTileStateBastion(state, checker)) {
                            protectedByBastion = true;
                            break outer;
                        }
                    }
                }
            }

            if (protectedByBastion) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        BastionEffect checker = new BastionEffect();
        List<Block> blocks = event.blockList();
        protectBlocksFromExplosion(blocks, checker);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        BastionEffect checker = new BastionEffect();
        List<Block> blocks = event.blockList();
        protectBlocksFromExplosion(blocks, checker);
    }
}
