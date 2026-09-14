package io.github.bluenova.boomba.events.events.FlintAndSteelExplode;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEatHotPotatoEvent implements Listener {

    @EventHandler
    public void onEatHotPotato(PlayerItemConsumeEvent event){
        ItemStack item = event.getItem();

        if (Boomba.getBoomEffectsManager().isItemHotPotato(item)) {
            Boomba.getBoomEffectsManager().playerAteHotPotato(event.getPlayer());
        }
    }

}
