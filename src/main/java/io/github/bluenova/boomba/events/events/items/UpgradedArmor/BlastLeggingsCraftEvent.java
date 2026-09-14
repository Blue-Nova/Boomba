package io.github.bluenova.boomba.events.events.items.UpgradedArmor;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class BlastLeggingsCraftEvent implements Listener {

    Material itemA = Material.CARROT;
    Material itemB = Material.DIAMOND_LEGGINGS;
    int amount = 10;
    Enchantment enchantment = Enchantment.BLAST_PROTECTION;

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
            meta.customName(UIManager.mini("<i><red>Boomba <white>Leggings"));
            meta.addEnchant(enchantment, 3, true);
            meta.addEnchant(Enchantment.UNBREAKING, 2, true);
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

        // Validate surrounding 8 slots are wool with amount exactly 32
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

        // Create result (enchanted boots)
        ItemStack result = new ItemStack(itemB);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.customName(UIManager.mini("<i><red>Boomba <white>Leggings"));
            meta.addEnchant(enchantment, 3, true);
            meta.addEnchant(Enchantment.UNBREAKING, 2, true);
            result.setItemMeta(meta);
        }

        // Try to give result to player (add to inventory, otherwise drop)
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(result);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        }

        // Manually consume 32 wool from each surrounding slot and 1 helmet (boots center)
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
}
