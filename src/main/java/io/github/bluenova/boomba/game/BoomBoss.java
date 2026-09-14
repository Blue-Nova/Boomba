package io.github.bluenova.boomba.game;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.events.events.BoomBoss.BoomBossListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

public class BoomBoss {

    // ── Constants ────────────────────────────────────────────────────────────
    private static final String LOG_PREFIX = "[BoomBoss]";

    /** HP fraction thresholds that trigger a phase transition. */
    private static final double PHASE_2_THRESHOLD = 0.66;
    private static final double PHASE_3_THRESHOLD = 0.33;

    /** Blocks from the bastion before a warning is shown. */
    public static final double LEASH_WARN_RANGE  = 20.0;
    /** Blocks from the bastion before the player is teleported back. */
    public static final double LEASH_HARD_RANGE  = 32.0;
    /** Smaller boss-only leash so the fight stays tighter around bastion. */
    private static final double BOSS_LEASH_HARD_RANGE = 18.0;

    /** Consecutive seconds a player must be outside LEASH_WARN_RANGE before punishment. */
    private static final int LEASH_GRACE_SECONDS = 3;

    // Phase 2 foundational tuning knobs
    private static final long PHASE_2_MINION_INTERVAL_TICKS = 20L * 5L;
    private static final int PHASE_2_MINION_CAP = 16;
    private static final double PHASE_2_MINION_SPAWN_RADIUS = 12.0;

    // Phase 3 foundational tuning knobs
    private static final long PHASE_3_CREEPER_INTERVAL_TICKS = 20L * 8L;
    private static final int PHASE_3_CREEPER_CAP = 6;
    private static final double PHASE_3_CREEPER_SPAWN_RADIUS = 12.0;

    // ── Persistent-data keys ─────────────────────────────────────────────────
    public static final NamespacedKey KEY_BOSS   = new NamespacedKey(Boomba.getInstance(), "boomboss_entity");
    public static final NamespacedKey KEY_MINION = new NamespacedKey(Boomba.getInstance(), "boomboss_minion");

    // ── Phase enum ───────────────────────────────────────────────────────────
    public enum Phase { PHASE_1, PHASE_2, PHASE_3, DEAD }

    // ── State ────────────────────────────────────────────────────────────────
    private final Wither wither;
    private final UUID   bossUUID;
    private Phase        currentPhase = Phase.PHASE_1;
    private BukkitTask   tickTask;
    private BukkitTask   phase2MinionTask;
    private BukkitTask   phase3CreeperTask;

    /** UUIDs of all living minions spawned by this boss. */
    private final Set<UUID> minionUUIDs = new HashSet<>();

    /** Tracks how many consecutive seconds each player has been outside leash range. */
    private final java.util.HashMap<UUID, Integer> outsideSeconds = new java.util.HashMap<>();

    /** Listener instance — held so we can unregister it on boss death. */
    private final BoomBossListener listener;

    /** The center location the boss and arena are anchored to. */
    private final Location arenaCenter;

    // ── Constructor ──────────────────────────────────────────────────────────
    public BoomBoss(Location spawnLocation) {
        this.arenaCenter = spawnLocation.clone();

        // Spawn and configure the wither
        wither = spawnLocation.getWorld().spawn(spawnLocation, Wither.class, w -> {
            w.customName(UIManager.mini("<bold><gradient:#ff4500:#ffd700>BoomBoss</gradient></bold>"));
            w.setCustomNameVisible(true);
            w.setInvulnerableTicks(20 * 11); // standard wither shield phase

            AttributeInstance maxHp = w.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) {
                maxHp.setBaseValue(maxHp.getBaseValue() * 1.5); // triple health
                w.setHealth(maxHp.getBaseValue());
            }

            // Tag this entity so the listener can identify it
            w.getPersistentDataContainer().set(KEY_BOSS, PersistentDataType.BYTE, (byte) 1);
        });

        bossUUID = wither.getUniqueId();

        // Register the event listener
        listener = new BoomBossListener(this);
        Boomba.getInstance().getServer().getPluginManager().registerEvents(listener, Boomba.getInstance());

        // Show the fight-start UI
        UIManager.showBossSpawned(Boomba.getAllPlayers(), Phase.PHASE_1);
        UIManager.updateBossBar(Boomba.getAllPlayers(), wither, Phase.PHASE_1);

        // Start the tick loop (every 20 ticks = 1 second)
        startTickLoop();

        Boomba.getInstance().getLogger().info(LOG_PREFIX + " BoomBoss spawned at " + spawnLocation);
    }

    // ── Tick loop ────────────────────────────────────────────────────────────
    private void startTickLoop() {
        tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (wither.isDead() || !wither.isValid()) {
                    // Handled by listener; cancel tick cleanly
                    cancel();
                    return;
                }
                onTick();
            }
        }.runTaskTimer(Boomba.getInstance(), 20L, 20L);
    }

    private void onTick() {
        checkPhaseTransition();
        enforceArena();
        snapBossToArena();
        UIManager.updateBossBar(Boomba.getAllPlayers(), wither, currentPhase);
    }

    // ── Phase transition ─────────────────────────────────────────────────────
    private void checkPhaseTransition() {
        if (currentPhase == Phase.DEAD) return;

        double maxHp = Objects.requireNonNull(wither.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
        double ratio = wither.getHealth() / maxHp;

        if (currentPhase == Phase.PHASE_1 && ratio <= PHASE_2_THRESHOLD) {
            transitionTo(Phase.PHASE_2);
        } else if (currentPhase == Phase.PHASE_2 && ratio <= PHASE_3_THRESHOLD) {
            transitionTo(Phase.PHASE_3);
        }
    }

    private void transitionTo(Phase next) {
        currentPhase = next;
        Boomba.getInstance().getLogger().info(LOG_PREFIX + " Transitioning to " + next);
        UIManager.showBossPhaseChange(Boomba.getAllPlayers(), next);

        switch (next) {
            case PHASE_2 -> onEnterPhase2();
            case PHASE_3 -> onEnterPhase3();
            default -> { /* no-op */ }
        }
    }

    /** Called once when Phase 2 begins — hook for attacks / minion spawning later. */
    protected void onEnterPhase2() {
        AttributeInstance movementSpeed = wither.getAttribute(Attribute.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(movementSpeed.getBaseValue() * 1.15);
        }

        AttributeInstance attackDamage = wither.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(attackDamage.getBaseValue() * 1.2);
        }

        startPhase2MinionSpawner();
    }

    /** Called once when Phase 3 begins — hook for full meltdown later. */
    protected void onEnterPhase3() {
        // Keep Phase 2 minions active; Phase 3 layers extra pressure on top.
        startPhase3CreeperSpawner();
        // TODO: rapid minion floods, TNT barrages, increased leash aggression
    }

    private void startPhase3CreeperSpawner() {
        stopPhase3CreeperSpawner();

        phase3CreeperTask = Bukkit.getScheduler().runTaskTimer(Boomba.getInstance(), () -> {
            if (currentPhase != Phase.PHASE_3) return;
            if (wither.isDead() || !wither.isValid()) return;

            int livingChargedCreepers = countLivingChargedCreepers();
            if (livingChargedCreepers >= PHASE_3_CREEPER_CAP) return;

            spawnPhase3ChargedCreeper();
        }, 20L * 3L, PHASE_3_CREEPER_INTERVAL_TICKS);
    }

    private void stopPhase3CreeperSpawner() {
        if (phase3CreeperTask == null) return;
        phase3CreeperTask.cancel();
        phase3CreeperTask = null;
    }

    private int countLivingChargedCreepers() {
        int count = 0;
        for (UUID id : minionUUIDs) {
            Entity entity = Bukkit.getEntity(id);
            if (entity instanceof Creeper creeper && creeper.isValid() && !creeper.isDead() && creeper.isPowered()) {
                count++;
            }
        }
        return count;
    }

    private void spawnPhase3ChargedCreeper() {
        World world = arenaCenter.getWorld();
        if (world == null) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        double offsetX = random.nextDouble(-PHASE_3_CREEPER_SPAWN_RADIUS, PHASE_3_CREEPER_SPAWN_RADIUS);
        double offsetZ = random.nextDouble(-PHASE_3_CREEPER_SPAWN_RADIUS, PHASE_3_CREEPER_SPAWN_RADIUS);

        Location spawnLoc = arenaCenter.clone().add(offsetX, 0, offsetZ);
        spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

        Creeper creeper = world.spawn(spawnLoc, Creeper.class, c -> {
            c.setPowered(true);
            c.setRemoveWhenFarAway(false);
            c.customName(UIManager.mini("<red><bold>Charged Minion"));
            c.setCustomNameVisible(false);
            c.setExplosionRadius(Math.max(3, c.getExplosionRadius()));
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                c.setTarget(Bukkit.getOnlinePlayers().iterator().next());
            }
        });

        registerMinion(creeper);
        Boomba.getInstance().getLogger().info(LOG_PREFIX + " Spawned Phase 3 charged creeper at " + spawnLoc.getBlockX() + "," + spawnLoc.getBlockY() + "," + spawnLoc.getBlockZ());
    }

    private void startPhase2MinionSpawner() {
        stopPhase2MinionSpawner();

        phase2MinionTask = Bukkit.getScheduler().runTaskTimer(Boomba.getInstance(), () -> {
            if (currentPhase != Phase.PHASE_2) return;
            if (wither.isDead() || !wither.isValid()) return;
            if (minionUUIDs.size() >= PHASE_2_MINION_CAP) return;
            spawnPhase2Minion();
        }, 20L * 5L, PHASE_2_MINION_INTERVAL_TICKS);
    }

    private void stopPhase2MinionSpawner() {
        if (phase2MinionTask == null) return;
        phase2MinionTask.cancel();
        phase2MinionTask = null;
    }

    private void spawnPhase2Minion() {
        World world = arenaCenter.getWorld();
        if (world == null) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        double offsetX = random.nextDouble(-PHASE_2_MINION_SPAWN_RADIUS, PHASE_2_MINION_SPAWN_RADIUS);
        double offsetZ = random.nextDouble(-PHASE_2_MINION_SPAWN_RADIUS, PHASE_2_MINION_SPAWN_RADIUS);

        Location spawnLoc = arenaCenter.clone().add(offsetX, 0, offsetZ);
        spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

        WitherSkeleton minion = world.spawn(spawnLoc, WitherSkeleton.class, skeleton -> {
            skeleton.customName(UIManager.mini("<dark_gray>Boom Minion"));
            skeleton.setCustomNameVisible(false);
            skeleton.setRemoveWhenFarAway(false);
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                skeleton.setTarget(Bukkit.getOnlinePlayers().iterator().next());
            }
        });

        registerMinion(minion);
        Boomba.getInstance().getLogger().info(LOG_PREFIX + " Spawned Phase 2 minion at " + spawnLoc.getBlockX() + "," + spawnLoc.getBlockY() + "," + spawnLoc.getBlockZ());
    }

    // ── Arena enforcement ────────────────────────────────────────────────────
    private void enforceArena() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            double dist = player.getLocation().distance(arenaCenter);
            if (player.getLocation().getY() > 300) continue;
            if (dist <= LEASH_WARN_RANGE) {
                // Back in range — reset their grace counter
                outsideSeconds.remove(player.getUniqueId());
                continue;
            }

            // Increment consecutive-seconds-outside counter
            int secs = outsideSeconds.merge(player.getUniqueId(), 1, Integer::sum);

            if (dist >= LEASH_HARD_RANGE || secs >= LEASH_GRACE_SECONDS) {
                // Punish: lightning + teleport back
                punishAndTeleport(player);
            } else {
                // Warn
                int remaining = LEASH_GRACE_SECONDS - secs;
                UIManager.showBossLeashWarning(player, remaining);
            }
        }
    }

    private void punishAndTeleport(Player player) {
        outsideSeconds.remove(player.getUniqueId());
        player.getWorld().strikeLightning(player.getLocation());
        player.teleport(arenaCenter.clone().add(0, 1, 0));
        UIManager.showBossLeashPunish(player);
        Boomba.getInstance().getLogger().info(LOG_PREFIX + " Pulled " + player.getName() + " back to arena.");
    }

    /** Prevent the boss from wandering too far from the bastion. */
    private void snapBossToArena() {
        if (wither.getLocation().distance(arenaCenter) > BOSS_LEASH_HARD_RANGE) {
            wither.teleport(arenaCenter.clone().add(0, 2, 0));
        }
    }

    // ── Boss death ───────────────────────────────────────────────────────────
    /** Called by BoomBossListener when the boss Wither dies. */
    public void onBossDeath() {
        currentPhase = Phase.DEAD;

        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }

        stopPhase2MinionSpawner();
        stopPhase3CreeperSpawner();

        // Kill all tracked minions
        killAllMinions();

        // Unregister the listener
        org.bukkit.event.HandlerList.unregisterAll(listener);

        outsideSeconds.clear();

        UIManager.showBossDefeated(Boomba.getAllPlayers());
        UIManager.hideBossBar(Boomba.getAllPlayers());

        Boomba.getInstance().getLogger().info(LOG_PREFIX + " BoomBoss defeated.");
    }

    // ── Minion helpers ───────────────────────────────────────────────────────
    public void registerMinion(Entity entity) {
        entity.getPersistentDataContainer().set(KEY_MINION, PersistentDataType.BYTE, (byte) 1);
        minionUUIDs.add(entity.getUniqueId());
    }

    /** Called by BoomBossListener when a tracked minion dies. */
    public void onMinionDeath(Entity entity) {
        minionUUIDs.remove(entity.getUniqueId());
        // Foundation behavior: minions pop with a small visual explosion.
        entity.getWorld().createExplosion(entity.getLocation(), 1.0f, false, false);
    }

    private void killAllMinions() {
        for (UUID id : new HashSet<>(minionUUIDs)) {
            Entity e = Bukkit.getEntity(id);
            if (e != null && !e.isDead()) e.remove();
        }
        minionUUIDs.clear();
    }

    // ── Accessors ────────────────────────────────────────────────────────────
    public Wither getWither()          { return wither; }
    public UUID   getBossUUID()        { return bossUUID; }
    public Phase  getCurrentPhase()    { return currentPhase; }
    public Location getArenaCenter()   { return arenaCenter.clone(); }
    public Set<UUID> getMinionUUIDs()  { return java.util.Collections.unmodifiableSet(minionUUIDs); }

    /** True if entity is this boss. */
    public static boolean isBossEntity(Entity entity) {
        return entity.getPersistentDataContainer().has(KEY_BOSS, PersistentDataType.BYTE);
    }

    /** True if entity is a tracked minion. */
    public static boolean isMinionEntity(Entity entity) {
        return entity.getPersistentDataContainer().has(KEY_MINION, PersistentDataType.BYTE);
    }
}
