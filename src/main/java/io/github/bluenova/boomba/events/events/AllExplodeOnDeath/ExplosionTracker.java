package io.github.bluenova.boomba.events.events.AllExplodeOnDeath;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ExplosionTracker {
    private static final List<Entry> explosions = new ArrayList<>();
    private static final long MAX_AGE_MS = 5_000; // keep explosions for 5 seconds

    private static class Entry {
        final Location loc;
        final long ts;

        Entry(Location loc, long ts) {
            this.loc = loc;
            this.ts = ts;
        }
    }

    public static synchronized void recordExplosion(Location loc) {
        explosions.add(new Entry(loc.clone(), System.currentTimeMillis()));
        cleanup();
    }

    public static synchronized Location getNearestRecent(Location query, double maxDistance) {
        cleanup();
        Location best = null;
        double bestSq = maxDistance * maxDistance;
        for (Entry e : explosions) {
            if (!e.loc.getWorld().equals(query.getWorld())) continue;
            double sq = e.loc.distanceSquared(query);
            if (sq <= bestSq) {
                bestSq = sq;
                best = e.loc;
            }
        }
        return best;
    }

    private static synchronized void cleanup() {
        long now = System.currentTimeMillis();
        explosions.removeIf(e -> now - e.ts > MAX_AGE_MS);
    }
}
