package io.github.bluenova.boomba.items;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.items.items.ExplosiveSack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemManager {
    private final static String logPrefix = "[ItemManager]";

    private final HashMap<String, BoomItem> boom_item_registery = new HashMap<>();


    public static final String EXPLOSIVE_SACK_ID= "explosive_sack";

    public ItemManager() {

        registerItem(EXPLOSIVE_SACK_ID, new ExplosiveSack());
        //registerItem(WOOL_SHOES, new WoolShoes());
        //registerItem(BLAST_CHESTPLATE, new BlastChestplate());
    }

    public void registerItem(String id, BoomItem item) {
        boom_item_registery.put(id, item);
    }

    public BoomItem getItem(String id) {
        return boom_item_registery.get(id);
    }

    // add checking method here to check if an item is the item that we want to check for.
    public boolean isItem(ItemStack itemStack, String id) {
        if (itemStack == null || itemStack.getItemMeta() == null) return false;
        if (!itemStack.getItemMeta().getPersistentDataContainer().has(Boomba.getBoombaKey(), PersistentDataType.STRING)) return false;
        String itemId = itemStack.getItemMeta().getPersistentDataContainer().get(Boomba.getBoombaKey(), PersistentDataType.STRING);
        Boomba.getInstance().log(logPrefix,"Checking item with ID: " + itemId + " against expected ID: " + id);
        return id.equals(itemId);
    }

    public void enableItem(String id) {
        BoomItem item = boom_item_registery.get(id);
        if (item != null) {
            item.enable();
        }
    }

    public void disableItem(String id) {
        BoomItem item = boom_item_registery.get(id);
        if (item != null) {
            item.disable();
        }
    }

    public static void handleExplosiveSackUse(Player player, ItemStack bundle) {
        if (bundle.getItemMeta() == null || !(bundle.getItemMeta() instanceof BundleMeta bundleMeta)) {
            Boomba.getInstance().log(logPrefix,"ItemMeta is null for the explosive sack! This should not happen.");
            return;
        }
        ArrayList<ItemStack> items = new ArrayList<>(bundleMeta.getItems());
        boolean foundCreeperEgg = false;
        for (ItemStack item : items) {
            if (item.getType() == Material.CREEPER_SPAWN_EGG) {
                foundCreeperEgg = true;
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                    break;
                }else {
                    items.remove(item);
                    bundleMeta.setItems(items);
                    bundle.setItemMeta(bundleMeta);
                    break;
                }
            }
        }
        if (!foundCreeperEgg) {
            Boomba.getInstance().log(logPrefix,"No creeper spawn egg found in the explosive sack! This should not happen.");
            return;
        }
        bundleMeta.setItems(items);
        // get the player's facing direction and shoot them in the opposite direction
        // this will create a knockback effect that pushes the player away from the explosion
        Vector facingDirection = player.getLocation().getDirection().normalize();
        Vector knockbackDirection = facingDirection.multiply(-2); // reverse the direction to push the player away
        player.setVelocity(player.getVelocity().add(knockbackDirection));
        // create an explosion at the player's looking direction
        player.getWorld().createExplosion(player.getLocation().add(facingDirection.multiply(0.1)), 0.0f, false, false);
    }

    public void isBlockBastion(Block block) {

    }
}
