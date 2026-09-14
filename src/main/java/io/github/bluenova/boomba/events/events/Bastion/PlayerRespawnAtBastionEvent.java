package io.github.bluenova.boomba.events.events.Bastion;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnAtBastionEvent implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (Boomba.getBoomEffectsManager().isBastionExists())
            e.setRespawnLocation(Boomba.getBoomEffectsManager().getBastionSpawnLocation());
        else
            Boomba.getInstance().getLogger().info("Bastion Not Found When Player Tried to Respawn: " + e.getPlayer().getName());
    }
}
