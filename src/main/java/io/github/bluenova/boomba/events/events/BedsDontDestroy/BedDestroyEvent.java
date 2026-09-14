package io.github.bluenova.boomba.events.events.BedsDontDestroy;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BedDestroyEvent implements Listener {

    @EventHandler
    public void onBedBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isBed(block)) {
            Boomba.getInstance().getLogger().info("Cancelled bed break event");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedDestroy(BlockDamageEvent event) {
        Block block = event.getBlock();
        if (isBed(block)) {
            Boomba.getInstance().getLogger().info("Cancelled bed damage event");

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        Boomba.getInstance().getLogger().info("Entity explode event detected with " + event.blockList().size() + " blocks");
        for (Block block : event.blockList()) {
            if (isBed(block)) {
                Material type = block.getType();
                BlockData data = block.getBlockData();
                event.blockList().remove(block); // remove bed from explosion block list to prevent it from being destroyed
                Bukkit.getScheduler().runTask(Boomba.getInstance(), () -> {
                    block.setType(type, false);
                    block.setBlockData(data, false);
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        Boomba.getInstance().getLogger().info("Block explode event detected with " + event.blockList().size() + " blocks");
        for (Block block : event.blockList()) {
            if (isBed(block)) {
                Material type = block.getType();
                BlockData data = block.getBlockData();
                event.blockList().remove(block); // remove bed from explosion block list to prevent it from being destroyed
                Bukkit.getScheduler().runTask(Boomba.getInstance(), () -> {
                    block.setType(type, false);
                    block.setBlockData(data, false);
                });
            }
        }
    }

    private boolean isBed(Block block) {
        Material material = block.getType();
        return material.name().endsWith("_BED");
    }

}
