package io.github.bluenova.boomba.tools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ShieldUtils {

    // Apply durability loss to the player's shield based on incoming damage amount.
    // Rounds damage up to an integer number of durability points. Removes/breaks the shield when durability exceeds max.
    public static void damageShield(Player player, double damage) {
        if (player == null) return;

        PlayerInventory inv = player.getInventory();
        ItemStack shield = inv.getItemInOffHand();
        boolean offhand = true;

        if (shield == null || shield.getType() != Material.SHIELD) {
            shield = inv.getItemInMainHand();
            offhand = false;
        }
        if (shield == null || shield.getType() != Material.SHIELD) return;

        ItemMeta meta = shield.getItemMeta();
        if (!(meta instanceof Damageable)) return;

        Damageable dmeta = (Damageable) meta;
        int add = (int) Math.ceil(damage);
        int newDamage = dmeta.getDamage() + add;
        dmeta.setDamage(newDamage);
        shield.setItemMeta(dmeta);

        // Max durability from material (deprecated in some APIs, but commonly available)
        int maxDurability = shield.getType().getMaxDurability();
        if (newDamage >= maxDurability) {
            // break the shield
            if (offhand) inv.setItemInOffHand(new ItemStack(Material.AIR));
            else inv.setItemInMainHand(new ItemStack(Material.AIR));
            // Optionally play break sound/particle here
        } else {
            // update inventory slot
            if (offhand) inv.setItemInOffHand(shield);
            else inv.setItemInMainHand(shield);
        }
    }
}
