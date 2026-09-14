package io.github.bluenova.boomba.events.events.Bastion;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PlaceBastionEvent implements Listener {

    @EventHandler
    public void onPlaceBastion(BlockPlaceEvent event) {
        // Only care about beacon placements
        if (event.getBlockPlaced().getType() != Material.BEACON) return;

        ItemStack item = event.getItemInHand();

        if (!item.hasItemMeta()) return;

        // Read the string identifier from the item (if present)
        String marker = item.getItemMeta()
                .getPersistentDataContainer()
                .get(Boomba.getBoombaKey(), PersistentDataType.STRING);

        if (marker == null) return;

        BlockState rawState = event.getBlockPlaced().getState();
        if (rawState instanceof Beacon beaconState) {
            if (Boomba.getBoomEffectsManager().isBastionExists()) {
                UIManager.showBastionPlaceDeny(event.getPlayer());
                event.setCancelled(true);
            }
            beaconState.getPersistentDataContainer().set(Boomba.getBoombaKey(), PersistentDataType.STRING, marker);
            beaconState.update(true);
            Boomba.getInstance().getLogger().info("Placed Bastion beacon; persistent marker transferred to block at "
                    + event.getBlockPlaced().getLocation());
            Boomba.getBoomEffectsManager().placeBastion(event.getBlockPlaced().getLocation());
        } else if (rawState instanceof TileState tile) {
            if (Boomba.getBoomEffectsManager().isBastionExists()) {
                UIManager.showBastionPlaceDeny(event.getPlayer());
                event.setCancelled(true);
            }
            tile.getPersistentDataContainer().set(Boomba.getBoombaKey(), PersistentDataType.STRING, marker);
            tile.update(true);
            Boomba.getInstance().getLogger().info("Placed Bastion beacon; persistent marker transferred to block at " + event.getBlockPlaced().getLocation());
            Boomba.getBoomEffectsManager().placeBastion(event.getBlockPlaced().getLocation());
        } else {
            Boomba.getInstance().getLogger().warning("Unable to transfer Bastion marker to placed block state; block state type=" + rawState.getClass().getName());
        }
    }
}
