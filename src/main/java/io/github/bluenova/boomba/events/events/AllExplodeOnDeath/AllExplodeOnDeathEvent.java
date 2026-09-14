package io.github.bluenova.boomba.events.events.AllExplodeOnDeath;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class AllExplodeOnDeathEvent implements Listener {

    private float hearts_per_explosion_power = ConfigManager.get_all_explode_on_death_hearts_per_explosion_power();
    private int difficulty_per_explosion_power = ConfigManager.get_all_explode_on_death_difficulty_per_explosion_power();

    double dropChance = 0.4; // 40% chance to drop a loot item on explosion

    EntityType[] blacklist = {
            EntityType.CREEPER,
            EntityType.PHANTOM
    };

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        for (EntityType entityType : blacklist) {
            if (event.getEntity().getType() == entityType) return;
        }
        int max_health = (int) Objects.requireNonNull(event.getEntity().getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
        float difficulty = Boomba.getBoomEffectsManager().getCurrentDifficulty();
        float scale = 1f + 0.1f * (difficulty / difficulty_per_explosion_power); // +10% per difficulty_per_explosion_power
        float min = 1.2f * scale;
        float max = (max_health / hearts_per_explosion_power) * scale;
        World world = event.getEntity().getWorld();
        world.playSound(event.getEntity().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1);
        world.spawnParticle(Particle.TOTEM_OF_UNDYING, event.getEntity().getLocation(), 5, 0.5, 0.5, 0.5, 0.1);
        ArrayList<ItemStack> drops = new ArrayList<>(event.getDrops());
        createExplosion(event, Math.max(min, max), drops);
        event.getDrops().clear();
    }

    // We delay the explosion to give the player time to react and then carry the drops to not lose them when the explosion happens.
    private void createExplosion(EntityDeathEvent event, float power, ArrayList<ItemStack> drops) {
        final EntityDeathEvent final_event = event;
        final Location loc = event.getEntity().getLocation();
        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), () -> {
            // record explosion before creating it so other listeners can find the source
            ExplosionTracker.recordExplosion(loc);
            final_event.getEntity().getWorld().createExplosion(loc, power, false, true);
                // 7% chance to drop a creeper spawn egg
            if (Math.random() < dropChance && event.getEntity().getType() != EntityType.PLAYER) {
                ItemStack lootItem = Boomba.getLootManager().getRandomLoot();
                final_event.getEntity().getWorld().dropItemNaturally(loc, lootItem);
                final_event.getEntity().getWorld().playSound(loc, Sound.ENTITY_ENDER_EYE_DEATH, 1f, 0.7f);
            }
            if (final_event.getEntity() instanceof Player player) {
                Boomba.getBoomEffectsManager().dropPlayerHeldDrops(player, loc);
            } else {
                for (ItemStack drop : drops) {
                    final_event.getEntity().getWorld().dropItemNaturally(loc, drop);
                }
            }
        }, 10L);
    }

}
