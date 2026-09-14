package io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.cutomshowers.PopInShower;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.cutomshowers.RandomShower;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.cutomshowers.SniperShower;
import io.github.bluenova.boomba.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TNTShowerEffect implements BoomEffect {

    String id = "tnt_shower";
    boolean active = false;

    BukkitTask bukkitTask;

    // Registry of available custom showers (pluggable)
    private final List<CustomShower> availableShowers = new ArrayList<>();

    private static final int CHANCE_PERCENTAGE = ConfigManager.get_tnt_shower_chance_percentage(); // 1% chance to trigger a shower every check
    private static final long COOLDOWN_SECONDS = ConfigManager.get_tnt_shower_cooldown_seconds(); // check every 120 seconds (adjust as needed)
    private static int added_percentage = 0;
    private static final int percentage_increase_on_fail = ConfigManager.get_tnt_shower_percentage_increase_on_fail(); // increase chance by 3% every time it fails to trigger, to ensure it eventually triggers

    // Task runs periodically to randomly trigger a shower on a random player
    Runnable task = () -> {
        if (!active) return;

        Random random = new Random();
        if (random.nextInt(100) < CHANCE_PERCENTAGE + added_percentage + (Boomba.getBoomEffectsManager().getCurrentDifficulty()/2)) { // 8% chance every check to trigger a shower
            Boomba.getInstance().getLogger().info("TNT Shower triggered!");
            triggerRandomShower();
            added_percentage = 0; // reset added percentage after triggering
        }else {
            added_percentage += percentage_increase_on_fail; // increase chance by 3% every time it fails to trigger, to ensure it eventually triggers
            Boomba.getInstance().getLogger().info("TNT Shower not triggered. Increased chance to " + (CHANCE_PERCENTAGE + added_percentage) + "%");
        }
    };

    public TNTShowerEffect() {
        // register built-in showers here; additional showers can be added later
        availableShowers.add(new RandomShower());
        availableShowers.add(new SniperShower());
        availableShowers.add(new PopInShower());
    }

    private void triggerRandomShower() {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOnline() && !Boomba.getBoomEffectsManager().isPlayerInShowers(p)) {
                players.add(p);
            }
        }
        Boomba.getInstance().getLogger().info("Online players: " + players.size());
        if (players.isEmpty()) {
            Boomba.getInstance().getLogger().info("No eligible players for TNT shower.");
            return; // no eligible players
        }

        Player pickedPlayer = Boomba.getBoomEffectsManager().getPlayerForShower();
        if (pickedPlayer == null) {
            Boomba.getInstance().getLogger().info("No player picked for TNT shower.");
            return; // safety check, should not happen since we check for empty list above
        }
        Boomba.getInstance().getLogger().info("Showering TNT on player: " + pickedPlayer.getName());
        List<CustomShower> sequence = pickRandomShowerSequence();
        Boomba.getBoomEffectsManager().addPlayerToShowers(pickedPlayer); // mark this player as being in a shower to prevent multiple showers at once
        runShowerSequence(pickedPlayer, sequence);
    }

    private List<CustomShower> pickRandomShowerSequence() {
        Random rnd = new Random();
        // 3 or 4 showers in a sequence
        int sequenceLength = (3 + (Boomba.getBoomEffectsManager().getCurrentDifficulty() / 4)) + rnd.nextInt(2 + (Boomba.getBoomEffectsManager().getCurrentDifficulty() / 6));
        List<CustomShower> sequence = new ArrayList<>();
        for (int i = 0; i < sequenceLength; i++) {
            CustomShower pick = availableShowers.get(rnd.nextInt(availableShowers.size())); // higher difficulty means higher chance to get more showers in the sequence
            sequence.add(pick);
        }
        Boomba.getInstance().getLogger().info("Picked TNT shower sequence of length " + sequence.size());
        return sequence;
    }

    private void runShowerSequence(Player player, List<CustomShower> sequence) {
        // Run the showers sequentially using callbacks, on the main thread. We schedule the first call on main thread.
        if (sequence == null || sequence.isEmpty()) return;
        player.sendActionBar(Component.text("A TNT shower is incoming! Brace yourself!"));
        player.playSound(player.getLocation(), Sound.ENTITY_GHAST_HURT, 1.0f, 0.4f);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.3f);
        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, 20, 10); // 10 seconds of slowness
        PotionEffect darkness = new PotionEffect(PotionEffectType.DARKNESS, 40, 1); // 10 seconds of blindness
        player.addPotionEffect(slowness);
        player.addPotionEffect(darkness);

        Runnable runner = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                // safety check
                if (index >= sequence.size()) return;

                CustomShower current = sequence.get(index);

                try {
                    // Start the current shower. Increment index only after this shower completes.
                    current.startShower(player, () -> {
                        // move to next after completion
                        index++;
                        Boomba.getInstance().getLogger().info("Completed shower " + index + "/" + sequence.size() + " for player " + player.getName());
                        if (index >= sequence.size()) {
                            Boomba.getInstance().getLogger().info("All showers completed for player " + player.getName());
                            afterAllShowers(player, sequence);
                            return;
                        }
                        // schedule next shower on main thread
                        Bukkit.getScheduler().runTask(Boomba.getInstance(), this);
                        Boomba.getInstance().getLogger().info("Started shower " + index + "/" + sequence.size() + " for player " + player.getName());
                    });
                } catch (Exception ex) {
                    Boomba.getInstance().getLogger().severe("Error while running shower: " + ex.getMessage());
                }
            }
        };
        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), runner, 40L); // slight delay before starting the sequence
    }

    private void startNextShower() {

    }

    @Override
    public void activate() {
        // Schedule the task and get the task id to cancel it later
        if (active) return; // already active
        bukkitTask = Bukkit.getScheduler().runTaskTimer(Boomba.getInstance(), task, 0L, 20 * COOLDOWN_SECONDS); // Run every 2 minutes
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return; // already inactive
        if (bukkitTask != null) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        }
        active = false;
    }

    @Override
    public String getInfo() {
        return "Randomized TNT showers on players";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    // Allow external code to register additional custom showers
    public void afterAllShowers(Player player, List<CustomShower> sequence) {
        Boomba.getInstance().getLogger().info(sequence.size() + " showers completed for player " + player.getName());
        player.sendActionBar(Component.text("The TNT shower has ended!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.4f);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.7f);

        PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 100, 1); // 5 seconds of regeneration
        PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION, 100, 1); // 5 seconds of saturation to help recover from hunger caused by explosions
        player.addPotionEffect(regeneration);
        player.addPotionEffect(saturation);
        Boomba.getBoomEffectsManager().removePlayerFromShowers(player); // allow this player to be showered again in the future
        Boomba.getBoomEffectsManager().removePlayerFromShowers(player);

        player.give(Boomba.getLootManager().getRandomLoot()); // give a random loot item as a consolation prize for surviving the shower
        player.give(Boomba.getLootManager().getRandomLoot()); // give a second random loot item as a consolation prize for surviving the shower
        player.give(Boomba.getLootManager().getRandomLoot()); // give a third random loot item as a consolation prize for surviving the shower

    }

}
