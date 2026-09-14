package io.github.bluenova.boomba.events.events.FlintAndSteelExplode;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerRightClickPlayerEvent implements Listener {

    @EventHandler
    public void onPlayerRightClickPlayer(PlayerInteractEntityEvent event) {
        // check if player is using flint and steel on another player
        Entity entity = event.getRightClicked();
        if (entity instanceof Player && Boomba.getBoomEffectsManager().isPlayerHoldingHotPotato(event.getPlayer())){
            // 7% to start hot potato on the player
            Boomba.getBoomEffectsManager().transferHotPotato(event.getPlayer(), (Player) entity);
        }
    }

}
