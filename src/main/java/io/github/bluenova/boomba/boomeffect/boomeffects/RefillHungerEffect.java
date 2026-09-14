package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class RefillHungerEffect implements BoomEffect {

    String id = "refill_hunger";
    boolean active = false;

    private int refillAmount = ConfigManager.get_refill_hunger_amount();
    private int cooldownSeconds = ConfigManager.get_refill_hunger_cooldown_seconds();

    Runnable task = () -> {
        // This effect is handled in the FallDamageExplodeEffect, so no need to do anything here.
        if (!active) return;
        addHunger();
    };

    BukkitTask bukkitTask;

    @Override
    public void activate() {
        if (active) return;
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Boomba.getInstance(), task, 0L, 20L *cooldownSeconds); // every 30 seconds
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return;
        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        active = false;
    }

    @Override
    public String getInfo() {
        return "Refills hunger when taking fall damage.";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void addHunger() {
        // get all players
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : players) {
            if (player.getFoodLevel() < 20) {
                player.setSaturation(Math.min(player.getSaturation() + ((float) (refillAmount * (Boomba.getBoomEffectsManager().getCurrentDifficulty() /6))), 20));
                player.setFoodLevel(Math.min(player.getFoodLevel() + ((refillAmount + (Boomba.getBoomEffectsManager().getCurrentDifficulty()/6))), 20));
            }
        }
    }
}
