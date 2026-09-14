package io.github.bluenova.boomba.events.events.MineFieldEffect;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnMineBreakEvent implements Listener {

    @EventHandler
    public void onMineInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        //
        if (block == null) return;
        Boomba.getBoomEffectsManager().checkIfMined(block, player, true);
    }

    // same as above but for BlockBreakEvent, to catch cases where players break the mined block with something like TNT or pistons instead of directly mining it. We want those to also trigger the explosion and not allow players to bypass the minefield by using indirect methods to break the block.
    @EventHandler
    public void onMineBreak(org.bukkit.event.block.BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Boomba.getBoomEffectsManager().checkIfMined(block, player, true);
    }

    // same as above but for BlockExplodeEvent, to catch cases where players break the mined block with something like TNT or pistons instead of directly mining it. We want those to also trigger the explosion and not allow players to bypass the minefield by using indirect methods to break the block.
    @EventHandler
    public void onMineExplode(BlockExplodeEvent event){
        for (Block block : event.blockList()) {
            Boomba.getBoomEffectsManager().checkIfMined(block, null, true);
        }
    }
}
