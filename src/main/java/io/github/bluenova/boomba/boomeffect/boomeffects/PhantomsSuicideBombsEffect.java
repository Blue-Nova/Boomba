package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

public class PhantomsSuicideBombsEffect implements BoomEffect {

    String id = "phantoms_suicide_bombs";
    boolean active = false;

    private final int CHANCE_PERCENTAGE = ConfigManager.get_phantom_suicide_bombers_chance_percentage();
    private int added_percentage = 0;
    private int percentage_increase_on_fail = ConfigManager.get_phantom_suicide_bombers_added_percentage_on_fail();
    private int attempt_cooldown_seconds = ConfigManager.get_phantom_suicide_bombers_attempt_cooldown_seconds();

    private int offset_above_target = 10;
    private int spawn_radius = 7;

    // Task runs periodically to randomly trigger a shower on a random player
    Runnable task = () -> {
        if (!active) return;
        Random random = new Random();
        if (random.nextInt(100) < CHANCE_PERCENTAGE + added_percentage) { // 8% chance every check to trigger a shower
            Boomba.getInstance().getLogger().info("Phantom Bombers triggered!");
            spawnPhantomSuicideBombers();
            added_percentage = 0; // reset added percentage after triggering
        }else {
            added_percentage += percentage_increase_on_fail; // increase chance by 3% every time it fails to trigger, to ensure it eventually triggers
            Boomba.getInstance().getLogger().info("Phantom Bombers not triggered. Increased chance to " + (CHANCE_PERCENTAGE + added_percentage) + "%");
        }
    };

    BukkitTask bukkitTask;

    @Override
    public void activate() {
        if (active) return; // Prevent double activation
        Boomba.getEventManager().activateEffectListeners(id);
        bukkitTask = Boomba.getInstance().getServer().getScheduler().runTaskTimer(Boomba.getInstance(), task, 0L, 20*55); // check every 25 seconds
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return; // Prevent double deactivation
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
        Boomba.getEventManager().deactivateEffectListeners(id);
        Boomba.getInstance().getServer().getScheduler().cancelTask(bukkitTask.getTaskId());
        active = false;
    }

    @Override
    public String getInfo() {
        return "Phant";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    private void spawnPhantomSuicideBombers() {
        // get all players that are NOT currently in showers
        ArrayList<Player> players = new ArrayList<>(Boomba.getBoomEffectsManager().getPlayersNotInShowers());
        if (players.isEmpty()) {
            Boomba.getInstance().getLogger().info("No eligible players for Phantom Bombers.");
            return;
        }
        // pick a random player to target
        Player target = players.get(new Random().nextInt(players.size()));

        // spawn phantoms around the target that will attack and explode on death
        int offsetX = (new Random().nextInt(2 * spawn_radius) - spawn_radius);
        int offsetZ = (new Random().nextInt(2 * spawn_radius) - spawn_radius);

        int ammountToSpawn = 1 + new Random().nextInt(3); // spawn 1-3 phantoms

        for (int i = 0; i < ammountToSpawn; i++) {
            target.getWorld().spawn(target.getLocation().add(offsetX, offset_above_target, offsetZ), Phantom.class, phantom -> {
                phantom.customName(Component.text("Phantom Bomber"));
                phantom.setCustomNameVisible(true);
                phantom.setShouldBurnInDay(false);
                phantom.setTarget(target);
            });
        }

        target.playSound(target.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 0.8f, 1.0f);
        target.playSound(target.getLocation(), Sound.ENTITY_TNT_PRIMED, 0.6f, 1.3f);
    }
}
