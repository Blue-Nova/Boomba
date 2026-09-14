package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class CreepersLayEggsEffect implements BoomEffect {

    String id = "creepers_lay_eggs";
    boolean active = false;

    private final NamespacedKey nextLayKey;
    private final Random random = new Random();

    // default interval (seconds) - can be later read from config
    private static final int MIN_SECONDS = ConfigManager.get_creepers_lay_eggs_min_seconds();
    private static final int MAX_SECONDS = ConfigManager.get_creepers_lay_eggs_max_seconds();

    private BukkitTask task;

    public CreepersLayEggsEffect() {
        this.nextLayKey = new NamespacedKey(Boomba.getInstance(), "creeper_next_egg_ts");
    }

    @Override
    public void activate() {
        if (active) return;
        Boomba.getEventManager().activateEffectListeners(id);
        startScheduler();
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return;
        if (task != null) task.cancel();
        Boomba.getEventManager().deactivateEffectListeners(id);
        active = false;
    }

    @Override
    public String getInfo() {
        return "Creepers periodically drop creeper spawn eggs using a per-entity PDC timestamp.";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    private void startScheduler() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (World world : Boomba.getInstance().getServer().getWorlds()) {
                    for (Entity e : world.getEntities()) {
                        if (e.getType() != EntityType.CREEPER) continue;
                        if (!(e instanceof Creeper creeper)) continue;
                        if (!creeper.isValid()) continue;

                        PersistentDataContainer pdc = creeper.getPersistentDataContainer();
                        if (!pdc.has(nextLayKey, PersistentDataType.LONG)) {
                            pdc.set(nextLayKey, PersistentDataType.LONG, now + randomDelayMillis());
                            // Boomba.getInstance().getLogger().info("Set next egg lay time for creeper " + creeper.getUniqueId() + " at " + (now + randomDelayMillis()));
                            continue;
                        }

                        Long next = pdc.get(nextLayKey, PersistentDataType.LONG);
                        if (next == null) {
                            pdc.set(nextLayKey, PersistentDataType.LONG, now + randomDelayMillis());
                            // Boomba.getInstance().getLogger().info("Initialized next egg lay time for creeper " + creeper.getUniqueId() + " at " + (now + randomDelayMillis()));
                            continue;
                        }

                        if (now >= next) {
                            Location loc = creeper.getLocation();
                            World w = creeper.getWorld();
                            w.dropItemNaturally(loc, new ItemStack(Material.CREEPER_SPAWN_EGG));
                            pdc.set(nextLayKey, PersistentDataType.LONG, now + randomDelayMillis());
                            w.playSound(loc, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                            // Boomba.getInstance().getLogger().info("Creeper " + creeper.getUniqueId() + " laid an egg. Next lay time set for " + (now + randomDelayMillis()));
                        }
                    }
                }
            }
        }.runTaskTimer(Boomba.getInstance(), 20L, 20L);
    }

    private long randomDelayMillis() {
        int sec = MIN_SECONDS + random.nextInt(MAX_SECONDS - MIN_SECONDS + 1);
        return sec * 1000L;
    }
}
