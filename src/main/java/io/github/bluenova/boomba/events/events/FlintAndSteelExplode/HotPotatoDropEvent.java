package io.github.bluenova.boomba.events.events.FlintAndSteelExplode;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class HotPotatoDropEvent implements Listener {

    @EventHandler
    public void onHotPotatoDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!Boomba.getBoomEffectsManager().isPlayerHoldingHotPotato(player)) return;
        ItemStack item = event.getItemDrop().getItemStack();
        if (Boomba.getBoomEffectsManager().isItemHotPotato(item)) {
            UIManager.showHotPotatoDropPotato(player);
            event.setCancelled(true);
        }

    }

}
