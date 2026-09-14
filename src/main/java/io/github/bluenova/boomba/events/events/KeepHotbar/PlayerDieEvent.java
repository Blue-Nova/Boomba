package io.github.bluenova.boomba.events.events.KeepHotbar;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerDieEvent implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // This event is triggered when a player dies. We can use it to prevent the hotbar from being cleared.
        // By default, Minecraft clears the player's inventory on death, but we want to keep the hotbar items.

        // Get the player who died
        Player player = event.getEntity();
        event.setKeepInventory(true); // This will keep the entire inventory, but we will manually drop the main inventory items
        event.getDrops().clear(); // Clear the drops to prevent duplication, we will handle dropping manually
        // remove the main inventory, slots 9-35, from the drops
        Inventory inventory = player.getInventory();
        ArrayList<ItemStack> mainInventoryItems = new ArrayList<>();
        for (int i = 9; i < inventory.getSize(); i++) {
            if (i >= 36) break; // Just in case, we only want to clear up to slot 35
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                Boomba.getInstance().getLogger().info("dropping item on death: " + item.getType() + " on slot:" + i);
                mainInventoryItems.add(item);

                inventory.setItem(i, null); // Clear the main inventory slots
            }
        }
        Boomba.getBoomEffectsManager().holdPlayerDrops(player, mainInventoryItems);
    }
}
