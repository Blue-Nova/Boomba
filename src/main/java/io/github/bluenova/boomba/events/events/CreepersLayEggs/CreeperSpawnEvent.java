package io.github.bluenova.boomba.events.events.CreepersLayEggs;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class CreeperSpawnEvent implements Listener {

    private final NamespacedKey nextLayKey;
    private final NamespacedKey neverDespawnKey;
    private final Random random = new Random();

    // default interval (seconds) - match the effect defaults
    private static final int MIN_SECONDS = 60;
    private static final int MAX_SECONDS = 300;

    public CreeperSpawnEvent() {
        this.nextLayKey = new NamespacedKey(Boomba.getInstance(), "creeper_next_egg_ts");
        this.neverDespawnKey = new NamespacedKey(Boomba.getInstance(), "creeper_never_despawn");
    }

    @EventHandler
    public void onCreeperSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.CREEPER) return;
        if (!(event.getEntity() instanceof Creeper c)) return;
        PersistentDataContainer pdc = c.getPersistentDataContainer();
        long next = System.currentTimeMillis() + randomDelayMillis();
        pdc.set(nextLayKey, PersistentDataType.LONG, next);

        // If spawned by a spawn egg (player used an egg), mark to never despawn
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            pdc.set(neverDespawnKey, PersistentDataType.BYTE, (byte) 1);
            try {
                c.setRemoveWhenFarAway(false);
            } catch (NoSuchMethodError ignored) {
                // Older API/version may not support setRemoveWhenFarAway; PDC still marks the entity.
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity e = event.getEntity();
        if (e.getType() != EntityType.CREEPER) return;
        PersistentDataContainer pdc = e.getPersistentDataContainer();
        if (pdc.has(nextLayKey, PersistentDataType.LONG)) {
            pdc.remove(nextLayKey);
        }
        if (pdc.has(neverDespawnKey, PersistentDataType.BYTE)) {
            pdc.remove(neverDespawnKey);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity e : event.getChunk().getEntities()) {
            if (e.getType() != EntityType.CREEPER) continue;
            if (!(e instanceof Creeper creeper)) continue;
            PersistentDataContainer pdc = creeper.getPersistentDataContainer();
            if (pdc.has(nextLayKey, PersistentDataType.LONG)) pdc.remove(nextLayKey);
            // intentionally keep neverDespawnKey through unload so it persists with the entity
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Reapply setRemoveWhenFarAway(false) for creepers that were marked never-despawn
        for (Entity e : event.getChunk().getEntities()) {
            if (e.getType() != EntityType.CREEPER) continue;
            if (!(e instanceof Creeper creeper)) continue;
            PersistentDataContainer pdc = creeper.getPersistentDataContainer();
            if (pdc.has(neverDespawnKey, PersistentDataType.BYTE)) {
                try {
                    creeper.setRemoveWhenFarAway(false);
                } catch (NoSuchMethodError ignored) {
                }
            }
        }
    }

    private long randomDelayMillis() {
        int sec = MIN_SECONDS + random.nextInt(MAX_SECONDS - MIN_SECONDS + 1);
        return sec * 1000L;
    }
}
