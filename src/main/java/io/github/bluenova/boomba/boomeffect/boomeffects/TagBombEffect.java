package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.bukkit.Bukkit.getServer;

public class TagBombEffect implements BoomEffect {

    String id = "tag_bomb";
    boolean active = false;

    Component name = Component.text("§cLaunch Bomb");

    private static final int FUSE_TICKS = 80; // 4 seconds
    private static final double LAUNCH_RADIUS = 6.0;
    private static final double LAUNCH_MULTIPLIER = 7;

    // PersistentDataContainer key used to track the Tag Bomb on both the item and the primed TNT entity
    private final NamespacedKey tagBombKey = new NamespacedKey(Boomba.getInstance(), "tag_bomb");

    @Override
    public void activate() {

        NamespacedKey key = new NamespacedKey(Boomba.getInstance(), "TagBomb");
        ItemStack item = ItemStack.of(Material.TNT, 1);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        List<Component> lore = new ArrayList<>();
        lore.add(UIManager.mini("<white>A volatile bomb that can be attached to entities or placed on the ground."));
        lore.add(UIManager.mini("<white>Right-click an entity to attach the bomb"));
        lore.add(UIManager.mini("<white>Place it to prime it instantly"));
        meta.lore(lore);
        // tag the item with a PersistentDataContainer entry so it can be tracked reliably
        meta.getPersistentDataContainer().set(tagBombKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("XXX", "CBC", "AAA");
        recipe.setIngredient('A', Material.CREEPER_SPAWN_EGG);
        recipe.setIngredient('B', Material.TNT);
        recipe.setIngredient('C', Material.COBBLED_DEEPSLATE);

        getServer().addRecipe(recipe);

        Boomba.getEventManager().activateEffectListeners(id);

        active = true;
    }

    @Override
    public void deactivate() {

        getServer().removeRecipe(new NamespacedKey(Boomba.getInstance(), "TagBomb"));
        Boomba.getEventManager().deactivateEffectListeners(id);
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

    // checks the item's PersistentDataContainer for the Tag Bomb tag
    public boolean isTagBombItem(ItemStack item) {
        if (item == null || item.getType() != Material.TNT || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(tagBombKey, PersistentDataType.BYTE);
    }

    // checks the entity's PersistentDataContainer for the Tag Bomb tag
    public boolean isTagBombEntity(Entity entity) {
        if (entity == null) return false;
        return entity.getPersistentDataContainer().has(tagBombKey, PersistentDataType.BYTE);
    }

    public void handleAttach(Player player, Entity rightClicked) {
        if (!active) return;
        if (player == null || rightClicked == null) return;

        // check if player is holding the Tag Bomb item (tracked via its PersistentDataContainer)
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (isTagBombItem(itemInHand)) {
            player.getInventory().getItemInMainHand().subtract(1); // consume one Tag Bomb item

            // run a task to create an explosion at the right-clicked entity's location after a short delay
            // every second, play a ticking sound to indicate the bomb is about to explode
            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= FUSE_TICKS) { // 4 seconds later (20 ticks per second)
                        createLaunchExplosion(rightClicked, Optional.of(0.1)); // create explosion that launches nearby players
                        cancel(); // stop only this task after explosion
                    } else {
                        // sound of ticking increases in pitch as it gets closer to explosion
                        float pitch = 1.0f + (ticks / (float) FUSE_TICKS); // pitch goes from 1.0 to 2.0
                        rightClicked.getWorld().playSound(rightClicked.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, pitch);
                    }
                    ticks += 20; // increment ticks by 20 (1 second)
                }
            }.runTaskTimer(Boomba.getInstance(), 0L, 20L);
        }
    }

    /**
     * Called when a Tag Bomb block is placed: instantly primes the TNT and plays the ticking tune.
     * The PersistentDataContainer tag is transferred from the item to the primed TNT entity so the
     * explosion can be identified later.
     */
    public void handlePlace(Player player, Block block, ItemStack itemUsed) {
        if (!active) return;
        if (player == null || block == null) return;
        if (!isTagBombItem(itemUsed)) return;

        // replace the placed block with an instantly-primed TNT entity
        Location spawnLoc = block.getLocation().add(0.5, 0, 0.5);
        block.setType(Material.AIR);

        TNTPrimed tnt = block.getWorld().spawn(spawnLoc, TNTPrimed.class, primed -> {
            primed.setFuseTicks(FUSE_TICKS);
            primed.setSource(player);
            // transfer the Tag Bomb tag from the item's PDC to the entity's PDC
            primed.getPersistentDataContainer().set(tagBombKey, PersistentDataType.BYTE, (byte) 1);
        });

        // play the same increasing-pitch ticking tune as the attach effect
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= FUSE_TICKS || tnt.isDead() || !tnt.isValid()) {
                    cancel();
                    return;
                }
                float pitch = 1.0f + (ticks / (float) FUSE_TICKS); // pitch goes from 1.0 to 2.0
                tnt.getWorld().playSound(tnt.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, pitch);
                ticks += 20;
            }
        }.runTaskTimer(Boomba.getInstance(), 0L, 20L);
    }

    /**
     * Called when a Tag Bomb primed TNT is about to explode. Returns true if this effect handled it,
     * meaning the vanilla explosion should be cancelled. Replaces the explosion with a harmless one
     * that launches nearby players upwards (y velocity multiplied by 5).
     */
    public boolean handleExplosionPrime(Entity entity) {
        if (!active) return false;
        if (!(entity instanceof TNTPrimed)) return false;
        if (!isTagBombEntity(entity)) return false;

        createLaunchExplosion(entity, Optional.empty());

        return true;
    }

    private void createLaunchExplosion(Entity entity, Optional<Double> optionalRadius) {
        Location loc = entity.getLocation();
        World world = loc.getWorld();

        // custom "explosion" - visual + audio only, no damage
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 10);

        // shoot nearby players up by multiplying their y velocity by 5
        for (Player p : loc.getNearbyPlayers(optionalRadius.orElse(LAUNCH_RADIUS))) {
            Vector vel = p.getVelocity();
            double baseY = Math.max(Math.abs(vel.getY()), 0.2); // ensure a meaningful base so idle players still launch
            vel.setY(baseY * LAUNCH_MULTIPLIER);
            p.setVelocity(vel);
        }
    }
}
