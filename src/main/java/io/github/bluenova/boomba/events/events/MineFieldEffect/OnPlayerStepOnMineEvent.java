package io.github.bluenova.boomba.events.events.MineFieldEffect;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerStepOnMineEvent implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Block block = player.getWorld().getBlockAt(player.getLocation().add(0,-0.5,0));
        Boomba.getBoomEffectsManager().checkIfMined(block, player, false);

        Boomba.getBoomEffectsManager().checkMinedPlayer(player);
    }
}
