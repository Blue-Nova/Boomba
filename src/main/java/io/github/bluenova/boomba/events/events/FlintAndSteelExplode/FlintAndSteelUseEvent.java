package io.github.bluenova.boomba.events.events.FlintAndSteelExplode;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class FlintAndSteelUseEvent implements Listener {

    HashMap<Material, Double> toolsMap = new HashMap<>();

    public FlintAndSteelUseEvent() {
        toolsMap.put(Material.FLINT_AND_STEEL, 0.07);

        toolsMap.put(Material.WOODEN_SHOVEL, 0.01);
        toolsMap.put(Material.STONE_SHOVEL, 0.01);
        toolsMap.put(Material.IRON_SHOVEL, 0.01);
        toolsMap.put(Material.DIAMOND_SHOVEL, 0.01);

        toolsMap.put(Material.SHEARS, 0.005);

        toolsMap.put(Material.WOODEN_HOE, 0.01);
        toolsMap.put(Material.STONE_HOE, 0.01);
        toolsMap.put(Material.IRON_HOE, 0.01);
        toolsMap.put(Material.DIAMOND_HOE, 0.01);

    }

    @EventHandler
    public void onFlintAndSteelUse(PlayerInteractEntityEvent event) {
        // check if player is using flint and steel on an entity
        Entity entity = event.getRightClicked();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (toolsMap.containsKey(item.getType()) && !(entity instanceof Player)){
            // 7% to start hot potato on the entity
            Double value = toolsMap.get(item.getType());
            if (Math.random() < value) {
                Boomba.getBoomEffectsManager().startHotPotato(event.getPlayer(), item, true);
            }
        }
    }

    @EventHandler
    public void onFlintAndSteelUse(PlayerInteractEvent event) {
        // check if player is using flint and steel on a block
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType().isAir()) return;
        if (toolsMap.containsKey(item.getType())){
            // 7% to start hot potato on the block
            Double value = toolsMap.get(item.getType());
            if (Math.random() < value) {
                Boomba.getBoomEffectsManager().startHotPotato(event.getPlayer(), item, true);
            }
        }
    }

}
