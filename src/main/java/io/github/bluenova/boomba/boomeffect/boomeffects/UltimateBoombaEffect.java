package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class UltimateBoombaEffect implements BoomEffect {

    private static final String log_prefix = "[UltimateBoombaEffect] ";

    private static final String EFFECT_ID = "ultimate_boomba";
    private static final String RECIPE_ID = "UltimateBoomba";
    private static final int FUSE_TICKS = 170; // comically long fuse duration... for... you know, comedic effect.

    private final NamespacedKey itemKey = new NamespacedKey(Boomba.getInstance(), "ultimate_boomba_item");
    private final NamespacedKey entityKey = new NamespacedKey(Boomba.getInstance(), "ultimate_boomba_entity");

    private final Set<String> placedUltimateBoombas = new HashSet<>();

    private boolean active = false;
    private boolean shutdownSequenceStarted = false;

    @Override
    public void activate() {
        if (active) return;

        ItemStack ultimateBoomba = createUltimateBoombaItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Boomba.getInstance(), RECIPE_ID), ultimateBoomba);
        recipe.shape("ONO", "NTN", "ONO");
        recipe.setIngredient('N', Material.TNT);
        recipe.setIngredient('T', Material.NETHER_STAR);
        recipe.setIngredient('O', Material.OBSIDIAN);
        Bukkit.addRecipe(recipe);

        Boomba.getEventManager().activateEffectListeners(EFFECT_ID);
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return;

        Bukkit.removeRecipe(new NamespacedKey(Boomba.getInstance(), RECIPE_ID));
        Boomba.getEventManager().deactivateEffectListeners(EFFECT_ID);
        placedUltimateBoombas.clear();
        shutdownSequenceStarted = false;
        active = false;
    }

    @Override
    public String getInfo() {
        return "A nether-star TNT that triggers a final BOOMBA shutdown sequence.";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void handlePlace(Player player, Block block, ItemStack itemUsed) {
        if (!active || player == null || block == null) return;

        if (!isUltimateBoombaItem(itemUsed)) {
            Boomba.getInstance().log(log_prefix, "Placed TNT without Ultimate BOOMBA tag at " + locationKey(block.getLocation()));
            return;
        }

        String key = locationKey(block.getLocation());
        placedUltimateBoombas.add(key);
        Boomba.getInstance().log(log_prefix, "Tracked Ultimate BOOMBA placement at " + key);
    }

    public void handlePrimedEntitySpawn(Entity entity) {
        if (!active) return;
        if (!(entity instanceof TNTPrimed tnt)) return;

        Location tntBlockLocation = tnt.getLocation().getBlock().getLocation();
        String exactKey = locationKey(tntBlockLocation);
        String belowKey = locationKey(tntBlockLocation.clone().subtract(0, 1, 0));

        String matchedKey = null;
        if (placedUltimateBoombas.contains(exactKey)) {
            matchedKey = exactKey;
        } else if (placedUltimateBoombas.contains(belowKey)) {
            matchedKey = belowKey;
        }

        if (matchedKey == null) return;

        placedUltimateBoombas.remove(matchedKey);
        tnt.setFuseTicks(FUSE_TICKS);
        tnt.getPersistentDataContainer().set(entityKey, PersistentDataType.BYTE, (byte) 1);
        Boomba.getInstance().log(log_prefix, "Tagged primed TNT as Ultimate BOOMBA at " + exactKey + " (matched " + matchedKey + ")");
    }

    public boolean handleExplosion(Entity entity) {
        if (!active) return false;
        if (!(entity instanceof TNTPrimed tnt)) return false;
        if (!tnt.getPersistentDataContainer().has(entityKey, PersistentDataType.BYTE)) return false;
        Boomba.getInstance().log(log_prefix,"Ultimate BOOMBA explosion detected at " + tnt.getLocation());
        startFinalBoomSequence(tnt.getLocation());
        return true;
    }

    private void startFinalBoomSequence(Location center) {
        if (shutdownSequenceStarted) return;
        shutdownSequenceStarted = true;

        World world = center.getWorld();
        if (world == null) return;

        announceUltimateBoom();

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > 120) {
                    kickEveryoneAndShutdown();
                    cancel();
                    return;
                }

                double radius = 2.0 + (tick / 12.0);
                spawnExplosionRing(world, center, radius, tick);
                spawnRandomExplosionsAroundPlayers();
                playMeltdownSoundscape();

                if (tick % 10 == 0) {
                    spawnTntRain(center);
                }

                if (tick % 20 == 0) {
                    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.7f + (tick / 120.0f));
                    world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 2);
                }

                tick += 5;
            }
        }.runTaskTimer(Boomba.getInstance(), 0L, 5L);
    }

    private void announceUltimateBoom() {
        Title title = Title.title(
                UIManager.mini("<bold><red>ULTIMATE BOOMBA</red></bold>"),
                UIManager.mini("<white>Meltdown Meltdown Meltdown Meltdown Meltdown Meltdown Meltdown Meltdown Meltdown"),
                Title.Times.times(Duration.ofMillis(300), Duration.ofMillis(2500), Duration.ofMillis(500))
        );

        ArrayList<Player> players = Boomba.getAllPlayers();
        for (Player player : players) {
            player.showTitle(title);
            player.sendActionBar(UIManager.mini("<red><bold>Take cover!</bold> <white>The final blast is unfolding."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.3f, 0.8f);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.7f);
        }
    }

    private void spawnExplosionRing(World world, Location center, double radius, int tick) {
        int points = 36;
        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2 * i) / points;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            double y = center.getY() + ((tick % 20) * 0.03);

            Location point = new Location(world, x, y, z);
            Vector outward = point.toVector().subtract(center.toVector()).normalize().multiply(0.15);

            world.spawnParticle(Particle.FLAME, point, 0, outward.getX(), 0.03, outward.getZ(), 0.1);
            world.spawnParticle(Particle.SMOKE, point, 1, 0.02, 0.03, 0.02, 0.01);

            if (i % 8 == 0 && tick % 20 == 0) {
                world.createExplosion(point, 1.5f, false, false);
            }
        }

        world.spawnParticle(Particle.LAVA, center, 8, radius * 0.1, 0.2, radius * 0.1, 0.01);
    }

    private void spawnTntRain(Location center) {
        World world = center.getWorld();
        if (world == null) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Rain a few TNT entities around the blast center.
        for (int i = 0; i < 8; i++) {
            double x = center.getX() + random.nextDouble(-18.0, 18.0);
            double y = center.getY() + random.nextDouble(14.0, 26.0);
            double z = center.getZ() + random.nextDouble(-18.0, 18.0);
            spawnMeltdownTnt(world, new Location(world, x, y, z), random);
        }

        // Also rain TNT near each player so everyone feels the chaos.
        for (Player player : Boomba.getAllPlayers()) {
            Location playerLoc = player.getLocation();
            for (int i = 0; i < 3; i++) {
                double x = playerLoc.getX() + random.nextDouble(-10.0, 10.0);
                double y = playerLoc.getY() + random.nextDouble(12.0, 22.0);
                double z = playerLoc.getZ() + random.nextDouble(-10.0, 10.0);
                spawnMeltdownTnt(world, new Location(world, x, y, z), random);
            }
        }
    }

    private void spawnMeltdownTnt(World world, Location spawnLocation, ThreadLocalRandom random) {
        TNTPrimed tnt = world.spawn(spawnLocation, TNTPrimed.class);
        // Keep fuse longer than meltdown so this acts as visual "TNT rain" while random explosions do damage.
        tnt.setFuseTicks(80);
        tnt.setVelocity(new Vector(
                random.nextDouble(-0.08, 0.08),
                random.nextDouble(-0.45, -0.2),
                random.nextDouble(-0.08, 0.08)
        ));
    }

    private void spawnRandomExplosionsAroundPlayers() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Player player : Boomba.getAllPlayers()) {
            if (random.nextDouble() > 0.7) continue;

            Location base = player.getLocation();
            World world = base.getWorld();
            if (world == null) continue;

            Location boomLoc = base.clone().add(
                    random.nextDouble(-8.0, 8.0),
                    random.nextDouble(0.0, 3.0),
                    random.nextDouble(-8.0, 8.0)
            );

            world.spawnParticle(Particle.EXPLOSION_EMITTER, boomLoc, 1);
            world.createExplosion(boomLoc, 2.5f, false, false);
        }
    }

    private void playMeltdownSoundscape() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Player player : Boomba.getAllPlayers()) {
            Location loc = player.getLocation();

            player.playSound(loc, Sound.ENTITY_TNT_PRIMED, 1.2f, (float) random.nextDouble(0.5, 1.4));
            player.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.9f, (float) random.nextDouble(0.6, 1.4));
            player.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, (float) random.nextDouble(0.5, 1.2));

            if (random.nextDouble() > 0.5) {
                player.playSound(loc, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.8f, (float) random.nextDouble(0.6, 1.2));
                player.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 0.7f, (float) random.nextDouble(0.7, 1.5));
            }
        }
    }

    private void kickEveryoneAndShutdown() {
        ArrayList<Player> players = Boomba.getAllPlayers();
        for (Player player : players) {
            player.kick(UIManager.mini("<bold><red>ULTIMATE BOOMBA! EXPLOSION BOOM!</red></bold><newline><white>BIG BOOM."));
        }

        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), Bukkit::shutdown, 20L);
    }

    private ItemStack createUltimateBoombaItem() {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(UIManager.mini("<bold><red>Ultimate BOOMBA</red></bold>"));
            meta.lore(List.of(
                    UIManager.mini("<white>Looks like normal TNT."),
                    UIManager.mini("<gray>But this one... We've never seen anything like it before.")
            ));
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isUltimateBoombaItem(ItemStack item) {
        if (item == null || item.getType() != Material.TNT || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(itemKey, PersistentDataType.BYTE);
    }

    private String locationKey(Location location) {
        if (location.getWorld() == null) return "null:0:0:0";
        return location.getWorld().getUID() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }
}

