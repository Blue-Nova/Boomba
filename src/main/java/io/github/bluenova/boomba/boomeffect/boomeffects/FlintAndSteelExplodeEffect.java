package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.guieffect.qual.UI;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlintAndSteelExplodeEffect implements BoomEffect {

    String id = "flint_and_steel_hot_potato";
    boolean active = false;

    int HOT_POTATO_DURATION_SECONDS = 15*2; // Duration of the hot potato in seconds

    ItemStack hotPotatoItem = new ItemStack(Material.POTATO); // The item that will be used as the hot potato, can be changed to any item

    HashMap<UUID,Player> playersWithHotPotato = new HashMap<>();
    ArrayList<Player> doomedPlayers = new ArrayList<>();

    List<Component> lore = List.of(
            UIManager.mini("<gray>Don't hold this for too long..."),
            UIManager.mini("<gray>Pass it to another player!"));

    @Override
    public void activate() {
        if (active) return; // Prevent double activation
        Boomba.getEventManager().activateEffectListeners(id); // Activate listeners for this effect
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return; // Prevent double deactivation
        Boomba.getEventManager().deactivateEffectListeners(id); // Deactivate listeners for this effect
        active = false;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void startHotPotato(Player player, boolean replace_hand) {
        // put the item and player in the hot potato lists
        if (!this.isActive()) return;
        if (playersWithHotPotato.containsValue(player)) return; // Player is already holding a hot potato, can't start another one on them
        // Generate a unique game UUID for this hot potato instance
        UUID gameUUID = UUID.randomUUID();
        playersWithHotPotato.put(gameUUID,player);
        UIManager.showHotPotatoStart(player, player.getInventory().getItemInMainHand().toString());
        // start a timer for 15 seconds, after which the item will explode and be removed from the hot potato lists

        AtomicInteger timer = new AtomicInteger(HOT_POTATO_DURATION_SECONDS);
        //replace the item in the player's hand with the hot potato item
        giveHotPotatoItem(player, replace_hand);

        BukkitTask task = Boomba.getInstance().getServer().getScheduler().runTaskTimer(Boomba.getInstance(), () -> {
            Player playerWithPotato = playersWithHotPotato.get(gameUUID);
            if (timer.get() <= 0) {
                // explode the item and remove it from the hot potato lists
                if (playerWithPotato != null) {
                    playerWithPotato.getWorld().createExplosion(playerWithPotato.getLocation(), 4.0f);
                    UIManager.showHotPotatoLost(playerWithPotato);
                }

                playersWithHotPotato.remove(gameUUID);
                doomedPlayers.remove(playerWithPotato);
                assert playerWithPotato != null;
                takeHotPotatoItem(playerWithPotato);
                // Cancel the scheduled task

            } else {
                timer.getAndDecrement();
                float pitch = 0.6f + (HOT_POTATO_DURATION_SECONDS - timer.get()) * 0.05f; // Increase pitch as timer goes down
                UIManager.showHotPotatoTimer(playerWithPotato, timer.get()/2, pitch);
            }

        }, 0, 10); // This will run every half a second for 15 seconds then explode

        // cancel the task after it finishes
        Boomba.getInstance().getServer().getScheduler().runTaskLater(Boomba.getInstance(), () -> {
            Boomba.getInstance().getServer().getScheduler().cancelTask(task.getTaskId());
        }, HOT_POTATO_DURATION_SECONDS * 20L); // Cancel the task after the hot potato duration plus a little buffer
    }

    private void giveHotPotatoItem(Player player, boolean addOriginalItemToInventory) {
        ItemStack originalItem = player.getInventory().getItemInMainHand();
        ItemStack potato = hotPotatoItem.clone(); // Clone the hot potato item to give to the player
        PersistentDataContainer dataContainer = potato.getItemMeta().getPersistentDataContainer();
        dataContainer.set(Boomba.getBoombaKey(), PersistentDataType.BOOLEAN, true); // Mark the item as a hot potato using persistent data container
        potato.setItemMeta(potato.getItemMeta()); // Update the item meta to save the persistent data
        // name the item "Hot Potato" in gold color
        potato.editMeta(meta -> {
            meta.displayName(UIManager.mini("<u><i><b><red>Hot <white>Potato</b></i></u>"));
            meta.setEnchantmentGlintOverride(true); // Add enchantment glint to make the item look special
        });
        potato.lore(lore);
        player.getInventory().setItemInMainHand(potato); // Set the hot potato item in the player's hand
        if (addOriginalItemToInventory) {
            player.getInventory().addItem(originalItem); // Add the original item back to the player's inventory (in case they had something in their hand)
        }
    }

    private void takeHotPotatoItem(Player player) {
        // find the hot potato item in the player's inventory and remove it
        if (player == null) return;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (isItemHotPotato(item)) {
                player.getInventory().setItem(i, null); // Remove the hot potato item from the player's inventory
                break; // There should only be one hot potato item in the player's inventory, so we can break after finding it
            }
        }
        // Also check the player's off hand
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (isItemHotPotato(offHandItem)) {
            player.getInventory().setItemInOffHand(null); // Remove the hot potato item from the player's off hand
        }
        // Also check the player's main hand just in case
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (isItemHotPotato(mainHandItem)) {
            player.getInventory().setItemInMainHand(null); // Remove the hot potato item from the player's main hand
        }
        // Also check the player's armor slots just in case
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (int i = 0; i < armorContents.length; i++) {
            if (isItemHotPotato(armorContents[i])) {
                player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null}); // Remove the hot potato item from the player's armor slots
                break; // There should only be one hot potato item in the player's inventory, so we can break after finding it
            }
        }
        // Also check Extra contents (like tridents in the off hand) just in case
        ItemStack[] extraContents = player.getInventory().getExtraContents();
        for (int i = 0; i < extraContents.length; i++) {
            if (isItemHotPotato(extraContents[i])) {
                player.getInventory().setExtraContents(new ItemStack[]{null, null, null, null}); // Remove the hot potato item from the player's extra contents
                break; // There should only be one hot potato item in the player's inventory, so we can break after finding it
            }
        }
    }

    public boolean isItemHotPotato(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != hotPotatoItem.getType()) return false;
        if (!item.hasItemMeta()) return false;
        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        boolean isItem = Boolean.TRUE.equals(dataContainer.get(Boomba.getBoombaKey(), PersistentDataType.BOOLEAN));

        // check if the item has the correct lore as well, just to be extra sure
        if (!isItem) {
            List<Component> itemLore = item.lore();
            if (itemLore != null && new HashSet<>(itemLore).containsAll(lore)) {
                return true;
            }
        }

        return isItem;
    }

    public boolean isPlayerHoldingHotPotato(Player player) {
        return playersWithHotPotato.containsValue(player);
    }

    public void switchHotPotato(Player oldPlayer, Player newPlayer) {
        // switch the hot potato item to another random player
        if (!this.isActive()) return;
        if (!playersWithHotPotato.containsValue(oldPlayer)) return; // Player must be holding the hot potato to switch it
        if (doomedPlayers.contains(oldPlayer)) return; // players that ate the potato cannot pass it
        // get game uuid for the old player
        UUID uuid = null;
        for (UUID key : playersWithHotPotato.keySet()) {
            if (playersWithHotPotato.get(key).equals(oldPlayer)) {
                uuid = key;
                break;
            }
        }
        if (uuid == null) {
            Boomba.getInstance().getLogger().warning("Could not find hot potato for player " + oldPlayer.getName());
            return; // This should never happen, but just in case
        }
        takeHotPotatoItem(oldPlayer);
        UIManager.showHotPotatoPassed(oldPlayer, newPlayer);
        // Switch the hot potato to the new player
        playersWithHotPotato.put(uuid, newPlayer);
        giveHotPotatoItem(newPlayer, true);
    }

    public void playerAteHotPotato(Player player) {
        if (!this.isActive()) return;
        if (!playersWithHotPotato.containsValue(player)) return;
        doomedPlayers.add(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*30,5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20*30,1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20*30,5));
        UIManager.showAteHotPotato(player);
    }
}
