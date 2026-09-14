package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.Random;

public class ChanceDropTNTEffect implements BoomEffect {

    String id = "chance_drop_tnt";
    boolean active = false;

    BukkitTask bukkitTask;

    final int CHANCE_PERCENTAGE = ConfigManager.get_chance_drop_tnt_chance_percent(); // X% chance to drop TNT every check
    final int CHECK_INTERVAL_SECONDS = 20*ConfigManager.get_chance_drop_tnt_check_interval_seconds(); // Check every 20 is one second * X seconds
    final int TNT_FUSE_TICKS = 100; // TNT will explode after 5 seconds is 60 ticks
    final int SLOWNESS_DURATION_TICKS = 120; // Slowness effect duration 120 is 6 seconds
    final int SLOWNESS_AMPLIFIER = 4; // Slowness effect amplifier

    // Task
    Runnable task = () -> {
        if (!active) return;

        Random random = new Random();
        if (random.nextInt(100) < CHANCE_PERCENTAGE + (Boomba.getBoomEffectsManager().getCurrentDifficulty()/2)) {
            dropTNT();
        }
    };

    @Override
    public void activate() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(Boomba.getInstance(), task , 0L, CHECK_INTERVAL_SECONDS);
        active = true;
    }

    @Override
    public void deactivate() {
        if (bukkitTask != null) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId()); // Cancel the scheduled task
            active = false;
        }
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    private void dropTNT() {
        try{
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                if (Boomba.getBoomEffectsManager().isPlayerInShowers(player)) return; // don't drop TNT on players that are currently in a shower
                PotionEffect slownessPotion = new PotionEffect(PotionEffectType.SLOWNESS, SLOWNESS_DURATION_TICKS, SLOWNESS_AMPLIFIER);
                player.addPotionEffect(slownessPotion);

                //don't let the player jump
                Attribute jump_height = Attribute.JUMP_STRENGTH;
                Objects.requireNonNull(player.getAttribute(jump_height)).setBaseValue(0.0);

                Attribute step_height = Attribute.STEP_HEIGHT;
                Objects.requireNonNull(player.getAttribute(step_height)).setBaseValue(1.0); // default step height is 0.6, set to 1 to prevent fall damage from TNT explosion

                // after slowness wears off, reset jump height
                Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), () -> {
                    Objects.requireNonNull(player.getAttribute(jump_height)).setBaseValue(0.42); // default jump strength
                    Objects.requireNonNull(player.getAttribute(step_height)).setBaseValue(0.6); // reset step height to default
                }, SLOWNESS_DURATION_TICKS);

                Location location = player.getLocation();
                TNTPrimed tnt = location.getWorld().spawn(location.add(0,4,0), TNTPrimed.class);
                tnt.setFuseTicks(TNT_FUSE_TICKS); // 1.5 seconds
                tnt.customName(MiniMessage.miniMessage().deserialize("<b>Hi :)</b>"));

                player.sendActionBar("§cA TNT has spawned above you!");
                player.playSound(location, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 1.0f, 2.0f);
                player.playSound(location, Sound.ENTITY_TNT_PRIMED, 8.0f, 1.5f);

            });
        } catch (Exception e) {
            Boomba.getInstance().getLogger().severe("No Players online for Effect: " + id);
        }
    }
}
