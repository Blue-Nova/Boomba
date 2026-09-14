package io.github.bluenova.boomba.events.events.OresExplode;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.config.ConfigManager;
import io.github.bluenova.boomba.events.events.AllExplodeOnDeath.ExplosionTracker;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayerMineOreEvent implements Listener {

    private final String log_prefix = "[PlayerMineOreEvent]";
    private final Map<Material, Float> ores = new HashMap<>();
    private final Random random = new Random();

    private final float ores_explode_coal_ore = ConfigManager.get_ores_explode_coal_ore();
    private final float ores_explode_iron_ore = ConfigManager.get_ores_explode_iron_ore();
    private final float ores_explode_gold_ore = ConfigManager.get_ores_explode_gold_ore();
    private final float ores_explode_diamond_ore = ConfigManager.get_ores_explode_diamond_ore();
    private final float ores_explode_emerald_ore = ConfigManager.get_ores_explode_emerald_ore();

    private final float ores_explode_nether_quartz_ore = ConfigManager.get_ores_explode_nether_quartz_ore();
    private final float ores_explode_deepslate_coal_ore = ConfigManager.get_ores_explode_deepslate_coal_ore();
    private final float ores_explode_deepslate_iron_ore = ConfigManager.get_ores_explode_deepslate_iron_ore();
    private final float ores_explode_deepslate_gold_ore = ConfigManager.get_ores_explode_deepslate_gold_ore();
    private final float ores_explode_deepslate_diamond_ore = ConfigManager.get_ores_explode_deepslate_diamond_ore();
    private final float ores_explode_deepslate_emerald_ore = ConfigManager.get_ores_explode_deepslate_emerald_ore();

    public PlayerMineOreEvent() {
        ores.put(Material.COAL_ORE, ores_explode_coal_ore);
        ores.put(Material.IRON_ORE, ores_explode_iron_ore);
        ores.put(Material.GOLD_ORE, ores_explode_gold_ore);
        ores.put(Material.DIAMOND_ORE, ores_explode_diamond_ore);
        ores.put(Material.EMERALD_ORE, ores_explode_emerald_ore);

        ores.put(Material.NETHER_QUARTZ_ORE, ores_explode_nether_quartz_ore);
        ores.put(Material.DEEPSLATE_COAL_ORE, ores_explode_deepslate_coal_ore);
        ores.put(Material.DEEPSLATE_IRON_ORE, ores_explode_deepslate_iron_ore);
        ores.put(Material.DEEPSLATE_GOLD_ORE, ores_explode_deepslate_gold_ore);
        ores.put(Material.DEEPSLATE_DIAMOND_ORE, ores_explode_deepslate_diamond_ore);
        ores.put(Material.DEEPSLATE_EMERALD_ORE, ores_explode_deepslate_emerald_ore);
    }

    @EventHandler
    public void onPlayerMineOre(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        if (!ores.containsKey(blockType)) return;

        float chance = ores.getOrDefault(blockType, 0.0f);
        //Boomba.getInstance().log(log_prefix, "Player " + player.getName() + " broke block " + blockType + ", checking for explosion with chance " + chance);

        if (random.nextFloat()*100 >= chance) return;

        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);

        // Explosion parameters
        float power = 4.0f; // adjust as needed

        // Register explosion in tracker so other systems (like shield-block handlers) can consult it.
        ExplosionTracker.recordExplosion(loc);

        // Create the actual explosion in the world (player as source so permissions/attribution work)

        // spawn particles and play ding sound to telegraph the explosion before it happens, giving players a chance to react
        event.getBlock().getWorld().spawnParticle(Particle.CRIT, loc, 5);
        event.getBlock().getWorld().spawnParticle(Particle.FLAME, loc, 10);
        event.getBlock().getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);

        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), () -> {
                    event.getBlock().getWorld().createExplosion(loc, power, false, true);
                }, 20L); // delay by 20 tick to ensure block break processing completes
    }
}
