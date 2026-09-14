package io.github.bluenova.boomba.events.events.EndermanExplosiveTeleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EndermanPlaceBlockEvent implements Listener {

    @EventHandler
    public void onEndermanPlaceBlock(EntityChangeBlockEvent event) {
        // This event is triggered when an Enderman places a block. Place a primed tnt at the location of the block that was placed by the Enderman, and then remove the block that was placed by the Enderman.
        if (event.getEntity() instanceof Enderman enderman) {
            Location location = event.getBlock().getLocation();
            event.getBlock().setType(Material.AIR);
            World world = location.getWorld();
            TNTPrimed tnt = world.spawn(location, TNTPrimed.class);
            tnt.setFuseTicks(40); // Set the fuse time of the TNT to 2 seconds (40 ticks)
        }
    }
}
