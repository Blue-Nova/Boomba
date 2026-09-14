package io.github.bluenova.boomba.events.events.items.UpgradedArmor;
import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;


public class BoomBowCraftEvent implements Listener {

    Material itemA = Material.OBSIDIAN;
    Material itemB = Material.BOW;
    int amount = 1;
    Enchantment enchantment = Enchantment.QUICK_CHARGE;
    Enchantment enchantment2 = Enchantment.UNBREAKING;

    private final NamespacedKey boomBowKey = new NamespacedKey(Boomba.getInstance(), "boombow");

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();
        if (matrix.length < 9) return;

        ItemStack center = matrix[4];
        if (center == null || center.getType() != itemB) return;

        for (int i = 0; i < matrix.length; i++) {
            if (i == 4) continue;
            ItemStack slot = matrix[i];
            if (slot == null) return;
            Material m = slot.getType();
            if (!m.equals(itemA)) return;
            if (slot.getAmount() != amount) return;
        }

        ItemStack result = new ItemStack(itemB);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.customName(UIManager.mini("<i><red>Boom<white>Bow"));
            meta.addEnchant(enchantment, 2, true);
            meta.addEnchant(enchantment2, 3, true);
            // mark as boombow in persistent data container
            meta.getPersistentDataContainer().set(boomBowKey, PersistentDataType.BYTE, (byte) 1);
            result.setItemMeta(meta);
        }

        inv.setResult(result);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Only care about result-slot clicks in a crafting inventory
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;
        if (!(event.getInventory() instanceof CraftingInventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        CraftingInventory inv = (CraftingInventory) event.getInventory();
        ItemStack[] matrix = inv.getMatrix();
        Boomba.getInstance().getLogger().info("InventoryClick on crafting result detected for Blast Leggings.");

        if (matrix.length < 9) return;

        ItemStack center = matrix[4];
        if (center == null || center.getType() != itemB) return;

        // Validate surrounding 8 slots are obsidian with amount exactly 1
        for (int i = 0; i < matrix.length; i++) {
            if (i == 4) continue;
            ItemStack slot = matrix[i];
            if (slot == null) return;
            Material m = slot.getType();
            if (!m.equals(itemA)) return;
            if (slot.getAmount() != amount) return;
        }

        // Cancel default click so we handle giving & consuming manually
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        // Create result (enchanted bow) and mark with PDC
        ItemStack result = new ItemStack(itemB);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.customName(UIManager.mini("<i><red>Boom<white>Bow"));
            meta.addEnchant(enchantment, 2, true);
            meta.addEnchant(enchantment2, 3, true);
            meta.getPersistentDataContainer().set(boomBowKey, PersistentDataType.BYTE, (byte) 1);
            result.setItemMeta(meta);
        }

        // Try to give result to player (add to inventory, otherwise drop)
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(result);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        }

        // Manually consume ingredients
        for (int i = 0; i < matrix.length; i++) {
            if (i == 4) {
                ItemStack c = matrix[4];
                if (c != null) {
                    int newAmt = c.getAmount() - 1;
                    matrix[4] = (newAmt > 0) ? new ItemStack(c.getType(), newAmt) : null;
                }
                continue;
            }
            ItemStack slot = matrix[i];
            if (slot != null) {
                int newAmt = slot.getAmount() - amount;
                Boomba.getInstance().getLogger().info("Consuming from slot " + i + ": " + slot.getType() + " x" + slot.getAmount() + " -> new amount: " + newAmt);
                matrix[i] = (newAmt > 0) ? new ItemStack(slot.getType(), newAmt) : null;
            }
        }

        // Apply modified matrix and clear preview
        Boomba.getInstance().getLogger().info("Applying modified crafting matrix after click");
        inv.setMatrix(matrix);
        inv.setResult(null);
    }

    // add another event for shooting with the now marked boombow to create an explosion on hit.
    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getProjectile() instanceof Arrow)) return;

        ItemStack bow = event.getBow();
        if (bow == null) return;

        ItemMeta meta = bow.getItemMeta();
        if (meta == null) return;

        Byte isBoomBow = meta.getPersistentDataContainer().get(boomBowKey, PersistentDataType.BYTE);
        if (isBoomBow != null && isBoomBow == (byte) 1) {
            // This is our boombow, so we want to create an explosion where the arrow lands.
            Arrow arrow = (Arrow) event.getProjectile();
            // add PDC marker to the arrow so we can identify it on hit
            arrow.getPersistentDataContainer().set(boomBowKey, PersistentDataType.BYTE, (byte) 1);
        }
    }

    // event handler for arrows when they hit something, if they have the boombow_arrow marker, create an explosion at the location.
    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getEntity();
        Byte isBoomBowArrow = arrow.getPersistentDataContainer().get(boomBowKey, PersistentDataType.BYTE);
        if (isBoomBowArrow != null && isBoomBowArrow == (byte) 1) {
            // Create explosion at arrow location with power 2, without breaking blocks or causing fire
            arrow.getWorld().createExplosion(arrow.getLocation(), 2.0f, false, true);
        }
    }
}
