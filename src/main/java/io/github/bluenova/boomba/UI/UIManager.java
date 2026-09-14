package io.github.bluenova.boomba.UI;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.game.BoomBoss;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UIManager {

    private static BossBar waveBossBar;
    private static final Set<UUID> barPlayers = new HashSet<>();

    // ── Boss Bar ─────────────────────────────────────────────────────────────
    private static BossBar bossBossBar;
    private static final Set<UUID> bossBarPlayers = new HashSet<>();

    public static void updateBossBar(ArrayList<Player> players, Wither wither, BoomBoss.Phase phase) {
        var maxHpAttr = wither.getAttribute(Attribute.MAX_HEALTH);
        double maxHp = (maxHpAttr != null) ? maxHpAttr.getBaseValue() : 1.0;
        float progress = (float) Math.max(0.0, Math.min(1.0, wither.getHealth() / maxHp));

        String phaseLabel = switch (phase) {
            case PHASE_1 -> "<green>Phase 1 – Awakening";
            case PHASE_2 -> "<yellow>Phase 2 – Enraged";
            case PHASE_3 -> "<red>Phase 3 – Meltdown";
            case DEAD    -> "<gray>Defeated";
        };

        BossBar.Color color = switch (phase) {
            case PHASE_1 -> BossBar.Color.GREEN;
            case PHASE_2 -> BossBar.Color.YELLOW;
            case PHASE_3, DEAD -> BossBar.Color.RED;
        };

        Component title = mini("<bold><gradient:#ff4500:#ffd700>BoomBoss</gradient></bold> <white>— " + phaseLabel);
        BossBar newBar = BossBar.bossBar(title, progress, color, BossBar.Overlay.NOTCHED_10);

        // Hide old bar
        if (bossBossBar != null && !bossBarPlayers.isEmpty()) {
            for (UUID u : new HashSet<>(bossBarPlayers)) {
                Player p = Boomba.getInstance().getServer().getPlayer(u);
                if (p != null) p.hideBossBar(bossBossBar);
            }
        }

        bossBossBar = newBar;
        bossBarPlayers.clear();
        for (Player p : players) {
            p.showBossBar(bossBossBar);
            bossBarPlayers.add(p.getUniqueId());
        }
    }

    public static void hideBossBar(ArrayList<Player> players) {
        if (bossBossBar == null) return;
        for (Player p : players) p.hideBossBar(bossBossBar);
        bossBossBar = null;
        bossBarPlayers.clear();
    }

    // ── Boss UI events ────────────────────────────────────────────────────────
    public static void showBossSpawned(ArrayList<Player> players, BoomBoss.Phase phase) {
        Title title = Title.title(
                mini("<bold><gradient:#ff4500:#ffd700>BOOMBOSS</gradient></bold>"),
                mini("<white>The final boss has awakened! Defend the bastion!"),
                Title.Times.times(Duration.ofMillis(400), Duration.ofMillis(3000), Duration.ofMillis(600))
        );
        for (Player p : players) {
            p.showTitle(title);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 0.9f);
        }
    }

    public static void showBossPhaseChange(ArrayList<Player> players, BoomBoss.Phase phase) {
        String subtitle = switch (phase) {
            case PHASE_2 -> "<yellow>The boss is enraged!";
            case PHASE_3 -> "<red><bold>MELTDOWN — Final phase!";
            default      -> "";
        };
        String titleStr = switch (phase) {
            case PHASE_2 -> "<bold><yellow>PHASE 2";
            case PHASE_3 -> "<bold><red>PHASE 3";
            default      -> "";
        };

        Title title = Title.title(
                mini(titleStr),
                mini(subtitle),
                Title.Times.times(Duration.ofMillis(300), Duration.ofMillis(2500), Duration.ofMillis(500))
        );
        for (Player p : players) {
            p.showTitle(title);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_HURT, 1.2f, 0.7f);
            p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 0.9f);
        }
    }

    public static void showBossDefeated(ArrayList<Player> players) {
        Title title = Title.title(
                mini("<bold><gold>BOOMBOSS DEFEATED!</gold></bold>"),
                mini("<green>You have survived the BOOMBA!"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(800))
        );
        for (Player p : players) {
            p.showTitle(title);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.0f, 1.2f);
        }
    }

    public static void showBossLeashWarning(Player player, int secondsRemaining) {
        player.sendActionBar(mini("<bold><red>⚠ Return to the Bastion! </red></bold><white>" + secondsRemaining + "s before you are pulled back!"));
    }

    public static void showBossLeashPunish(Player player) {
        Title title = Title.title(
                mini("<bold><red>STAY CLOSE!</red></bold>"),
                mini("<white>You strayed too far from the Bastion."),
                Title.Times.times(Duration.ofMillis(200), Duration.ofMillis(1500), Duration.ofMillis(400))
        );
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
    }

    public static Component mini(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    public static void showHotPotatoLost(Player player) {
        Title title = Title.title(
                mini("<bold><red>B<white>OO<red>M!"),
                mini(""),
                Title.Times.times(
                        java.time.Duration.ofMillis(250),
                        java.time.Duration.ofMillis(1500),
                        java.time.Duration.ofMillis(750)
                )
        );

        player.showTitle(title);
        player.sendActionBar(mini("<bold><white>Your <red>Hot Potato<white> exploded!"));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1.0f, 0.8f);

    }

    public static void showHotPotatoTimer(Player playerWithPotato, int i, float pitch) {
        if (i <= 3)
            playerWithPotato.sendActionBar(mini("<i><bold><red>" + i + "..."));
        else
            playerWithPotato.sendActionBar(mini("<bold><white>Your <red>Hot Potato<white> will explode in <red>" + i + "s<white>!"));
        playerWithPotato.getWorld().playSound(playerWithPotato.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.8f, pitch);
    }

    public static void showHotPotatoStart(Player player, String ItemName) {
        Title title = Title.title(
                mini("<bold><white>HOT </white><red>POTATO</red><white>!"),
                mini("<white>Your " + ItemName + " will EXPLODE!<red> Pass<white> it to <red>another player!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(250),
                        java.time.Duration.ofMillis(2500),
                        java.time.Duration.ofMillis(750)
                )
        );

        player.showTitle(title);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 1.2f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 0.8f, 1f);
    }

    public static void showHotPotatoDropPotato(Player player) {
        Title title = Title.title(
                mini("<bold><red>No Littering!"),
                mini("<red>ONLY <white>give it to <red>someone else<white>!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(250),
                        java.time.Duration.ofMillis(1500),
                        java.time.Duration.ofMillis(750)
                )
        );
        player.closeInventory();
        player.showTitle(title);

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 0.6f);
    }

    public static void showHotPotatoPassed(Player oldPlayer, Player newPlayer) {
        Title title = Title.title(
                mini(""),
                mini("<white>You passed the <red>Hot Potato<white> to <gold>" + newPlayer.getName() + "<white>!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(100),
                        java.time.Duration.ofMillis(1000),
                        java.time.Duration.ofMillis(200)
                )
        );

        oldPlayer.showTitle(title);
        oldPlayer.playSound(oldPlayer.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 0.8f, 1.2f);
        oldPlayer.sendActionBar(Component.empty());

        Title newPlayerTitle = Title.title(
                mini("<bold><red>TAG!"),
                mini("<white>You got the <red>Hot Potato<white>! Pass it to <red>another player<white>!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(100),
                        java.time.Duration.ofMillis(1000),
                        java.time.Duration.ofMillis(250)
                )
        );

        newPlayer.showTitle(newPlayerTitle);
        newPlayer.getWorld().playSound(newPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 1.2f);
    }

    public static void showNextEffectEnabled( Player player, String key) {
        player.sendMessage(mini("<green>New Boom Effect Activated: <white>" + key));
    }
    public static void showNextEffectEnabled(String key) {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player p : players) p.sendMessage(mini("<green>New Boom Effect Activated: <white>" + key));
    }

    public static void announceDifficulty(int currentDifficulty) {
        Title title = Title.title(
                mini("<bold><red>BOOMBA!"),
                mini("<white>Difficulty increased!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(150),
                        java.time.Duration.ofMillis(1000),
                        java.time.Duration.ofMillis(500)
                )
        );

        ArrayList<Player> players = new ArrayList<>(Boomba.getInstance().getServer().getOnlinePlayers());
        for (Player player : players) {
            player.showTitle(title);
            player.sendActionBar(mini("<bold><red>Current Difficulty: <white>"+ currentDifficulty));
            player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.8f, 1.1f);
            player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 0.8f, 0.4f);
        }

    }

    public static void confirmDifficulty(Player player) {
        player.sendActionBar(mini("<red>Difficulty increased<white>:" + " level " + Boomba.getBoomEffectsManager().getCurrentDifficulty()));
    }

    public static void startGame(ArrayList<Player> playerArrayListy) {
        Title title = Title.title(
                mini("<bold><gray>BOOMBA!"),
                mini("<white>Yo ass bouta blow up!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(150),
                        java.time.Duration.ofMillis(1000),
                        java.time.Duration.ofMillis(500)
                )
        );

        for (Player player : playerArrayListy){
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1,1);
        }
    }

    public static void updateWave(ArrayList<Player> players, int current, int max) {
        if (max <= 0) return;

        // initialize audiences lazily


        // progress fills up as current approaches 0
        float progress = (float) (1.0 - ((float) current / (float) max));
        progress = (float) Math.max(0.0, Math.min(1.0, progress));

        String titleStr = current <= 0 ? "Wave starting!" : "Next wave in " + current + "s";
        Component titleComp = mini(titleStr);

        // choose color using Adventure's BossBar.Color
        BossBar.Color color = BossBar.Color.GREEN;
        if (progress >= 0.8f) color = BossBar.Color.RED;
        else if (progress >= 0.4f) color = BossBar.Color.YELLOW;

        // create new boss bar instance for this update
        BossBar newBar = BossBar.bossBar(titleComp, progress, color, BossBar.Overlay.PROGRESS);

        // hide old bar from everyone who had it
        BossBar oldBar = waveBossBar;
        if (oldBar != null && !barPlayers.isEmpty()) {
            for (UUID u : new HashSet<>(barPlayers)) {
                Player p = Boomba.getInstance().getServer().getPlayer(u);
                if (p != null) p.hideBossBar(oldBar);
            }
        }

        // replace stored bar
        waveBossBar = newBar;

        // show new bar to provided players and track them
        barPlayers.clear();
        for (Player p : players) {
            p.showBossBar(waveBossBar);
            barPlayers.add(p.getUniqueId());
        }

        // If countdown reached zero or there are no players, remove bar completely
        if (current <= 0 || players.isEmpty()) {
            if (waveBossBar != null) {
                for (UUID u : new HashSet<>(barPlayers)) {
                    Player p = Boomba.getInstance().getServer().getPlayer(u);
                    if (p != null) p.hideBossBar(waveBossBar);
                }
            }
            waveBossBar = null;
            barPlayers.clear();
        }
    }

    public static void showBastionPlaced(ArrayList<Player> allPlayers , Location loc) {
        for (Player p : allPlayers){
            p.sendActionBar(mini("<blue>Your <bold>Bastion</bold> has placed at: <white>" + loc.getBlockX() + "X - " + loc.getBlockY() + "Y - " + loc.getBlockZ() + "Z"));
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1,1);
        }
    }

    public static void showBastionPlaceDeny(Player player) {
        player.sendActionBar(mini("<red><b>You cannot place any more Bastions!"));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
    }

    public static void updateBastionBlock(Location loc, Player p) {
        p.sendActionBar(mini("<blue>Updated bastion block to: <white>" + loc.getBlockX() + "X - " + loc.getBlockY() + "Y - " + loc.getBlockZ() + "Z"));

    }

    public static void showUpgradeBastion(int range) {
        ArrayList<Player> players = new ArrayList<>(Boomba.getAllPlayers());
        for (Player p: players){
            p.sendActionBar(mini("<blue>Your <bold>Bastion</bold> is now Level:<white>" + range));
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f,1);
        }
    }

    public static void showCurrentBastionUpgradeProgress(Material material, int newNeeded, int bastionUpgradeAmount) {
        ArrayList<Player> players = new ArrayList<>(Boomba.getAllPlayers());
        for (Player p: players){
            p.sendActionBar(mini("<blue>Bastion Upgraded: <white>" + material.name() + " " + (bastionUpgradeAmount - newNeeded) + "/" + bastionUpgradeAmount));
        }
    }

    public static void showAteHotPotato(Player player) {
        Title title = Title.title(
                mini("<bold><red>Oh uh..."),
                mini("<white>I don't think I should'a done that!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(500),
                        java.time.Duration.ofMillis(1500),
                        java.time.Duration.ofMillis(1500)
                )
        );
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.3f);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, 1f,0.4f);
    }

    public static void showBastionMaxLevel(Player player) {
        player.sendActionBar(mini("<red><i>The <b>Bastion</b> has reached its MAX Level!"));
    }

    public static void showStepOnMineTitle(Player player) {
        Title title = Title.title(
                mini("<bold><white>Click!"),
                mini("<white>You triggered a mine!"),
                Title.Times.times(
                        java.time.Duration.ofMillis(150),
                        java.time.Duration.ofMillis(1500),
                        java.time.Duration.ofMillis(150)
                )
        );

        player.showTitle(title);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2.0f, 1.8f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 0.8f, 0.6f);
    }

    public static void showEndermenWaveTitle() {
        Title title = Title.title(
                mini("<shadow:#FF5555><bold><color:#ff1cf0>Endermen Wave!</color>"),
                mini("<white>A wave of endermen is approaching..."),
                Title.Times.times(
                        java.time.Duration.ofMillis(1000),
                        java.time.Duration.ofMillis(2000),
                        java.time.Duration.ofMillis(500)
                )
        );

        ArrayList<Player> players = new ArrayList<>(Boomba.getInstance().getServer().getOnlinePlayers());
        for (Player player : players) {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 0.8f);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1, 1.2f);
        }
    }
}
