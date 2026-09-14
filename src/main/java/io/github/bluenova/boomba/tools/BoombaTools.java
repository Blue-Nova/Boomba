package io.github.bluenova.boomba.tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class BoombaTools {

    public static Player pickRandomPlayer() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.size() <= 0) return null;
        Random rnd = new Random();
        return players.get(rnd.nextInt(0,players.size()));
    }

    public static Block pickSurfaceBlockInRange(Location playerLocation, int range) {
        if (playerLocation == null || playerLocation.getWorld() == null || range < 0) return null;

        World world = playerLocation.getWorld();
        int centerX = playerLocation.getBlockX();
        int centerY = playerLocation.getBlockY();
        int centerZ = playerLocation.getBlockZ();
        double rangeSq = (double) range * range;

        ArrayList<Block> candidates = new ArrayList<>();

        int worldMinY = world.getMinHeight();
        int worldMaxY = world.getMaxHeight(); // exclusive upper bound

        // Ensure the "above" block exists: y + 1 must be <= worldMaxY - 1 -> y <= worldMaxY - 2
        int scanMinY = Math.max(worldMinY, centerY - range);
        int scanMaxY = Math.min(worldMaxY - 2, centerY + range);

        if (scanMinY > scanMaxY) return null; // no valid Y to scan

        for (int x = centerX - range; x <= centerX + range; x++) {
            for (int z = centerZ - range; z <= centerZ + range; z++) {
                double dx = x + 0.5 - playerLocation.getX();
                double dz = z + 0.5 - playerLocation.getZ();
                if (dx * dx + dz * dz > rangeSq) continue;

                // scan the column within the Y window for any block that has air directly above it
                for (int y = scanMaxY; y >= scanMinY; y--) {
                    Block below = world.getBlockAt(x, y, z);
                    Block above = world.getBlockAt(x, y + 1, z);

                    if (below.getType().equals(Material.WATER) || below.getType().equals(Material.LAVA)) continue;
                    if (above.getType() == Material.AIR && below.getType() != Material.AIR) {
                        candidates.add(below);
                        break; // for this column we found a valid surface block; move to next column
                    }
                }
            }
        }

        if (candidates.isEmpty()) return null;
        Random rnd = new Random();
        return candidates.get(rnd.nextInt(candidates.size()));
    }

}
