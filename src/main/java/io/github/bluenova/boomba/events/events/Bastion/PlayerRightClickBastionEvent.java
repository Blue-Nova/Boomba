package io.github.bluenova.boomba.events.events.Bastion;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.boomeffects.BastionEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerRightClickBastionEvent implements Listener {

    final ArrayList<Material> allowedUpgrades = new ArrayList<>();

    public PlayerRightClickBastionEvent() {
        allowedUpgrades.add(Material.SAND);
        allowedUpgrades.add(Material.DIRT);
        allowedUpgrades.add(Material.COBBLESTONE);
        allowedUpgrades.add(Material.GRAVEL);
        allowedUpgrades.add(Material.COBBLED_DEEPSLATE);
        allowedUpgrades.add(Material.DIORITE);
        allowedUpgrades.add(Material.GRANITE);
        allowedUpgrades.add(Material.ANDESITE);
    }

    @EventHandler
    public void RightClickWithItemEvent(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if (block == null){
            return;
        }
        BlockState state = block.getState();
        if (state instanceof TileState tile) {
            if (!BastionEffect.isBastionBlock(tile.getPersistentDataContainer())) {
                return;
            }
            event.setCancelled(true);
            Player player = event.getPlayer();
            // if there is not current bastion, is this a bastion? if yes, make it the main bastian.
            // in case of a mid-game server restart.
            if (!Boomba.getBoomEffectsManager().isBastionExists()){
                Boomba.getBoomEffectsManager().setBastionBlock(block.getLocation(), player);
            }

            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            for (Material material : allowedUpgrades){
                if (itemInHand.getType().equals(material)){
                    Boomba.getBoomEffectsManager().attemptUpgradeBastion(player, itemInHand);
                    break;
                }
            }

        }

    }

}
