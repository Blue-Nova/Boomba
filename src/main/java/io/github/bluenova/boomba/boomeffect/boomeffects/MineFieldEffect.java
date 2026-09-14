package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.BoomEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import io.github.bluenova.boomba.tools.BoombaTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MineFieldEffect implements BoomEffect {

    private final String log_prefix = "[MineFieldEffect]";
    private final boolean debug = false;
    String id = "mine_field";
    boolean active = false;

    private int get_mine_field_cooldown_seconds = (20* ConfigManager.get_mine_field_cooldown_seconds()); // default 30 seconds, can be later read from config
    private float explosion_power = ConfigManager.get_mine_field_explosion_power(); // default 4.0f (TNT explosion), can be later read from config

    HashMap<Block, Player> minedBlocks = new HashMap<>();
    ArrayList<Player> warnedPlayers = new ArrayList<>();

    BukkitTask bukkitTask;

    Runnable task = () -> {
        if (!active) return;
        Player player = BoombaTools.pickRandomPlayer();
        if (player == null) return;
        Location playerLocation = player.getLocation();
        Block block = BoombaTools.pickSurfaceBlockInRange(playerLocation, 5);
        while (isBlockMined(block)){
            block = BoombaTools.pickSurfaceBlockInRange(playerLocation, 5);
        }
        Boomba.getInstance().log(log_prefix, "Attempting to mine block at: X:" + block.getLocation().x() + " Y:" + block.getLocation().y() + " Z:" + block.getLocation().z());
        mineBlock(block);
    };

    private void mineBlock(Block block) {
        if (!active) return;
        // Mark block with PDC boolean (store as BYTE = 1). Silently skip if PDC unsupported.
        // BlockState state = block.getState();
        try {
            // Only TileState (including Beacon) supports persistent data containers
            //Boomba.getInstance().log(log_prefix, "checking tileState...");
            /*if (!(state instanceof TileState tileState)) return;
            ((TileState) state).getPersistentDataContainer();
            Boomba.getInstance().log(log_prefix, "Block has TileState!");
            PersistentDataContainer pdc = tileState.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Boomba.getInstance(), "mined");
            pdc.set(key, PersistentDataType.BYTE, (byte) 1);
            state.update(true, false);*/

            minedBlocks.put(block, null);

        } catch (Exception ignored) {
            // Some block states may not support persistent data; ignore failure.
            Boomba.getInstance().log(log_prefix, ignored.getMessage());
        }
    }

    public boolean isBlockMined(Block block) {
        if (minedBlocks.containsKey(block)) return true;
        return false;
    }

    @Override
    public void activate() {
        if (active) return;
        active = true;

        bukkitTask = Bukkit.getScheduler().runTaskTimer(Boomba.getInstance(), task, get_mine_field_cooldown_seconds, get_mine_field_cooldown_seconds);
        Boomba.getEventManager().activateEffectListeners("mine_field");
    }

    @Override
    public void deactivate() {
        if (!active) return;
        active = false;
        bukkitTask.cancel();
        Boomba.getEventManager().deactivateEffectListeners("mine_field");
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void checkIfMined(Block block, Player player, boolean explodeInstantly) {
        // Boomba.getInstance().log(log_prefix, "Checking block:" + block.getType() + " with player:" + player.getName());
        if (player == null){
            // when player is null, the mine was triggered by something other than a player stepping on it (e.g. piston, explosion chain reaction, etc). In this case we want to explode the mine instantly without warning.
            // Boomba.getInstance().log(log_prefix, "Player is null, treating as explosion triggered by non-player cause.");
            if (isBlockMined(block)) {
                Boomba.getInstance().log(log_prefix, "Exploding block at X:" + block.getLocation().x() + " Y:" + block.getLocation().y() + " Z:" + block.getLocation().z() + " due to non-player trigger.");
                minedBlocks.remove(block);
                explodeMine(block, null);
                return;
            }
            return;
        }

        if (isBlockMined(block)) {
            // Boomba.getInstance().log(log_prefix, "Detected Mined Block at X:" + block.getLocation().x() + " Y:" + block.getLocation().y() + " Z:" + block.getLocation().z());
            if (warnedPlayers.contains(player)) {
                Boomba.getInstance().log(log_prefix, "Player " + player.getName() + " already warned, skipping.");
                return;
            }
            UIManager.showStepOnMineTitle(player);
            if (explodeInstantly) {
                Boomba.getInstance().log(log_prefix, "Exploding instantly due to event trigger.");
                explodeMine(block, player);
                minedBlocks.remove(block);
                return;
            }
            minedBlocks.replace(block, player);
            warnedPlayers.add(player);
        }
    }

    public void checkMinedPlayer(Player player) {

        Block blockBelowPlayer = player.getLocation()
                .clone()
                .subtract(0, 0.25, 0)
                .getBlock();

        /*Boomba.getInstance().log(log_prefix,
                "Block below player: "
                        + blockBelowPlayer.getType()
                        + " at "
                        + blockBelowPlayer.getLocation());*/

        if (minedBlocks.isEmpty()) {
            return;
        }

        Block minedBlock = null;

        for (Map.Entry<Block, Player> entry : minedBlocks.entrySet()) {

            Block tempBlock = entry.getKey();
            Player tempPlayer = entry.getValue();

            /*Boomba.getInstance().log(log_prefix,
                    "Checking map entry -> Block: "
                            + tempBlock.getType()
                            + " at "
                            + tempBlock.getLocation()
                            + ", Player: "
                            + (tempPlayer == null ? "null" : tempPlayer.getName()));*/

            // null safety
            if (tempPlayer == null) {
                /*Boomba.getInstance().log(log_prefix,
                        "Skipped because player is null");*/
                continue;
            }

            // wrong player
            if (!tempPlayer.getUniqueId().equals(player.getUniqueId())) {
                /*Boomba.getInstance().log(log_prefix,
                        "Skipped because player does not match");*/
                continue;
            }

            Boomba.getInstance().log(log_prefix,
                    "Matched player!");

            // player is STILL standing on the mined block
            if (tempBlock.getLocation().equals(blockBelowPlayer.getLocation())) {

                /*Boomba.getInstance().log(log_prefix,
                        "Player is still standing on mined block");*/

                continue;
            }

            Boomba.getInstance().log(log_prefix,
                    "PLAYER LEFT THE BLOCK!");

            minedBlock = tempBlock;
            break;
        }

        if (minedBlock == null) {

            /*Boomba.getInstance().log(log_prefix,
                    "No mined block found to explode");*/

            return;
        }

        Boomba.getInstance().log(log_prefix,
                "EXPLODING BLOCK at "
                        + minedBlock.getLocation());

        explodeMine(minedBlock, player);

        minedBlocks.remove(minedBlock);
        warnedPlayers.remove(player);

        Boomba.getInstance().log(log_prefix,
                "Removed exploded block from map");
    }

    private void explodeMine(Block minedBlock, Player player) {
        Location loc = minedBlock.getLocation().add(0.5, 1.1, 0.5);
        if (player != null)
            // if player is not null, move the explosion slightly towards the player to increase the chance they get caught in it, since the explosion is triggered by them stepping off the block
            // the max adjustment should be 1 block.
            loc = loc.add(Math.max(-1, Math.min(1, player.getLocation().getX() - loc.getX())),
                    0, Math.max(-1, Math.min(1, player.getLocation().getZ() - loc.getZ())));

        minedBlock.getWorld().createExplosion(
                loc,
                explosion_power,
                true,
                true
        );
    }
}
