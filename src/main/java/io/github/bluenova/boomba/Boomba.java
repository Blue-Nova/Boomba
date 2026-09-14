package io.github.bluenova.boomba;

import io.github.bluenova.boomba.boomeffect.BoomEffectsManager;
import io.github.bluenova.boomba.commands.MainCommand;
import io.github.bluenova.boomba.config.ConfigManager;
import io.github.bluenova.boomba.events.EventManager;
import io.github.bluenova.boomba.game.LootManager;
import io.github.bluenova.boomba.game.WaveManager;
import io.github.bluenova.boomba.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class Boomba extends JavaPlugin {

    private static Boomba instance;

    private static BoomEffectsManager boomEffectsManager;
    private static EventManager eventManager;
    private static ItemManager itemManager;
    private static WaveManager waveManager;
    private static LootManager lootManager;

    private static MainCommand mainCommand;

    public static NamespacedKey getBoombaKey(String id) {
        return new NamespacedKey(instance, "boomba_" + id);
    }
    public static NamespacedKey getBoombaKey() {
        return new NamespacedKey(instance, "boomba");
    }

    public static ArrayList<Player> getAllPlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        ConfigManager.load();
        eventManager = new EventManager();
        itemManager = new ItemManager();
        boomEffectsManager = new BoomEffectsManager();
        mainCommand = new MainCommand();
        waveManager = new WaveManager();
        lootManager = new LootManager();
        Objects.requireNonNull(getCommand("boomba")).setExecutor(mainCommand);
        Objects.requireNonNull(getCommand("boomba")).setTabCompleter(mainCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Boomba getInstance() {
        return instance;
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static BoomEffectsManager getBoomEffectsManager() {
        return boomEffectsManager;
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static WaveManager getWaveManager(){
        return waveManager;
    }

    public static LootManager getLootManager() {
        return lootManager;
    }

    public void log(String prefix, String msg) {
        getLogger().info(prefix + " >> "+ msg);
    }
}
