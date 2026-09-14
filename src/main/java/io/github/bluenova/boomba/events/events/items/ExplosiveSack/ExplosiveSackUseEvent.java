package io.github.bluenova.boomba.events.events.items.ExplosiveSack;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.items.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ExplosiveSackUseEvent implements Listener {
    final static String log_prefix = "[ExplosiveSackUse_Event]";

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        // get the item in main hand
        //Boomba.getInstance().log(log_prefix, "event action is:" + event.getAction());
        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;
        if (itemStack.getItemMeta() == null) return;

        // check if the item is the explosive sack
        if (Boomba.getItemManager().isItem(itemStack, ItemManager.EXPLOSIVE_SACK_ID)) {
            // if it is, cancel the event and trigger the explosion
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
            // trigger the explosion here (you can customize the explosion effect as needed
            // explosive sacks are bundles. They only contain creeper eggs.
            // reduce the amount of creeper eggs in the bundle by 1.
            //Boomba.getInstance().log(log_prefix, "firing explosive sack use with event type:" + event.getEventName());
            ItemManager.handleExplosiveSackUse(event.getPlayer(), itemStack);
        }
    }
}
