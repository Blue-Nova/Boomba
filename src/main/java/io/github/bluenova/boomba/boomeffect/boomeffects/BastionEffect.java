package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class BastionEffect implements BoomEffect {

    static String id = "bastion";
    boolean active = false;

    // Prepared ItemStack that represents the custom Bastion beacon
    private final ItemStack bastionItem;

    // Lore shown on the item (adventure Component list)
    private final List<Component> lore = List.of(
            UIManager.mini("<gray>A mysterious beacon that creates a bastion when placed."),
            UIManager.mini("<gray>Right-click to place a protected bastion block.")
    );

    public BastionEffect() {
        // prepare the beacon item when the effect instance is constructed
        this.bastionItem = createBastionItem();
    }

    @Override
    public void activate() {
        // Activate listeners (if you have any) for this effect

        NamespacedKey key = new NamespacedKey(Boomba.getInstance(), "Bastion");

        ShapedRecipe recipe = new ShapedRecipe(key, bastionItem);
        recipe.shape("ABA", "ACA", "AAA");
        recipe.setIngredient('A', Material.IRON_INGOT);
        recipe.setIngredient('B', Material.COAL_BLOCK);
        recipe.setIngredient('C', Material.CREEPER_SPAWN_EGG);

        getServer().addRecipe(recipe);

        Boomba.getEventManager().activateEffectListeners(id);
        active = true;
    }

    @Override
    public void deactivate() {
        // Deactivate listeners for this effect
        getServer().removeRecipe(new NamespacedKey(Boomba.getInstance(), "Bastion"));
        Boomba.getEventManager().deactivateEffectListeners(id);
        active = false;
    }

    @Override
    public String getInfo() {
        return "Bastion: A beacon that, when placed, creates a protected bastion area. (Foundational implementation)";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    // Create and return a properly tagged beacon ItemStack for this plugin
    private ItemStack createBastionItem() {
        ItemStack beacon = new ItemStack(Material.BEACON);

        // Use editMeta to set display name and persistent data in one place
        beacon.editMeta(meta -> {
            // store the identifier string so we can identify which custom item this is
            meta.getPersistentDataContainer().set(Boomba.getBoombaKey(), PersistentDataType.STRING, id);

            // set friendly name + visual glint
            meta.displayName(UIManager.mini("<gold>Bastion Beacon"));
            meta.setEnchantmentGlintOverride(true);

            // set lore
            meta.lore(lore);
        });

        return beacon;
    }

    // Check whether a given ItemStack is the custom Bastion beacon
    public boolean isItemBastion(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.BEACON) return false;
        if (!item.hasItemMeta()) return false;

        // check persistent data first (now a STRING identifier)
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        String marker = dataContainer.get(Boomba.getBoombaKey(), PersistentDataType.STRING);
        if (id.equals(marker)) {
            Boomba.getInstance().getLogger().info("Identified Bastion item via persistent data marker: " + marker);
            return true;
        }

        // fallback: check lore contents
        List<Component> itemLore = item.lore();
        if (itemLore != null && itemLore.containsAll(lore)) {
            return true;
        }
        return false;
    }

    public static boolean isBastionBlock(PersistentDataContainer dataContainer) {
        String marker = dataContainer.get(Boomba.getBoombaKey(), PersistentDataType.STRING);
        boolean found = id.equals(marker);
        /*if (found) {
            Boomba.getInstance().getLogger().info("Identified Bastion block via persistent data marker: " + marker);
        }*/
        return found;
    }
}
