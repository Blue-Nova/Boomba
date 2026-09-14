package io.github.bluenova.boomba.game;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class LootManager {

    ArrayList<ItemStack> loot = new ArrayList<>();

    public LootManager() {
        // farming items
        registerLoot(new ItemStack(Material.POTATO,2));
        registerLoot(new ItemStack(Material.CARROT,2));
        registerLoot(new ItemStack(Material.POTATO,2));
        registerLoot(new ItemStack(Material.CARROT,2));

        // animal spawn eggs
        registerLoot(new ItemStack(Material.COW_SPAWN_EGG,2));
        registerLoot(new ItemStack(Material.SHEEP_SPAWN_EGG,2));
        registerLoot(new ItemStack(Material.CHICKEN_SPAWN_EGG,2));
        registerLoot(new ItemStack(Material.PIG_SPAWN_EGG,2));
        registerLoot(new ItemStack(Material.CREEPER_SPAWN_EGG,2));

        // ores
        registerLoot(new ItemStack(Material.DIAMOND, 1));
        registerLoot(new ItemStack(Material.IRON_INGOT, 4));
        registerLoot(new ItemStack(Material.GOLD_INGOT, 2));
        registerLoot(new ItemStack(Material.DIAMOND, 1));
        registerLoot(new ItemStack(Material.IRON_INGOT, 4));
        registerLoot(new ItemStack(Material.GOLD_INGOT, 2));

        registerLoot(new ItemStack(Material.ARROW, 8));
        registerLoot(new ItemStack(Material.ARROW, 8));

        registerLoot(new ItemStack(Material.BREAD, 8));
        registerLoot(new ItemStack(Material.APPLE, 6));

        registerLoot(new ItemStack(Material.BREAD, 8));
        registerLoot(new ItemStack(Material.APPLE, 6));

        registerLoot(new ItemStack(Material.BONE, 8));
        registerLoot(new ItemStack(Material.STRING, 8));
        registerLoot(new ItemStack(Material.FLINT, 12));

        registerLoot(new ItemStack(Material.BONE, 8));
        registerLoot(new ItemStack(Material.STRING, 8));
        registerLoot(new ItemStack(Material.FLINT, 12));

        registerLoot(new ItemStack(Material.OAK_SAPLING, 4));
        registerLoot(new ItemStack(Material.SPRUCE_SAPLING, 4));
        registerLoot(new ItemStack(Material.BIRCH_SAPLING, 4));

        registerLoot(new ItemStack(Material.OAK_SAPLING, 4));
        registerLoot(new ItemStack(Material.SPRUCE_SAPLING, 4));
        registerLoot(new ItemStack(Material.BIRCH_SAPLING, 4));

        registerLoot(new ItemStack(Material.SLIME_BLOCK, 4));
        registerLoot(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
        registerLoot(new ItemStack(Material.GOLDEN_APPLE, 3));
        registerLoot(new ItemStack(Material.ENDER_PEARL, 4));
    }

    private void registerLoot(ItemStack item) {
        loot.add(item);
    }

    public ItemStack getRandomLoot() {
        int index = (int) (Math.random() * loot.size());
        return loot.get(index);
    }
}
