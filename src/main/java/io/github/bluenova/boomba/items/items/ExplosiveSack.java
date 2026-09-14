package io.github.bluenova.boomba.items.items;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.items.BoomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ExplosiveSack extends BoomItem {

    public ExplosiveSack() {
        super("explosive_sack", "Explosive Sack", "A sack that explodes when used.");
    }

    @Override
    public ItemStack createTemplateItem() {
        ItemStack item = new ItemStack(Material.BUNDLE, 1);
        // safely get the ItemMeta, modify it, then set it back on the ItemStack
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component nameComponent = Component.text(getName());
            meta.displayName(nameComponent);
            Component descriptionComponent = Component.text(getDescription());
            meta.lore(List.of(descriptionComponent));

            // add a persistent data container to the item meta to identify it as an explosive sack
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            dataContainer.set(Boomba.getBoombaKey(), PersistentDataType.STRING, getId());

            item.setItemMeta(meta);
        }

        Boomba.getInstance().getLogger().info("Created template item for " + item.getType().name() + " with ID: " + getId());

        return item;
    }

    @Override
    public void enable() {
        if (active) return;

        // add the crafting recipe for the explosive sack
        ShapedRecipe explosiveSackRecipe = new ShapedRecipe(Boomba.getBoombaKey(getId()), this.createTemplateItem());
        // Define the shape of the recipe and the ingredients
        // we need 3 gunpowder, 1 creeper spawn egg, 1 leather, and 1 sand to craft the explosive sack
        // empty spaces in the recipe will be represented by 'X'
        explosiveSackRecipe.shape("XXX", "GCL", "XSX");
        explosiveSackRecipe.setIngredient('G', Material.GUNPOWDER);
        explosiveSackRecipe.setIngredient('C', Material.CREEPER_SPAWN_EGG);
        explosiveSackRecipe.setIngredient('L', Material.LEATHER);
        explosiveSackRecipe.setIngredient('S', Material.SAND);
        Boomba.getInstance().getServer().addRecipe(explosiveSackRecipe);

        this.active = true;
    }

    @Override
    public void disable() {
        if (!active) return;
        Boomba.getInstance().getServer().removeRecipe(Boomba.getBoombaKey(getId())); // Remove the recipe for the explosive sack
        this.active = false;
    }
}
