package io.github.bluenova.boomba.events.events.BoomBoss;

import io.github.bluenova.boomba.game.BoomBoss;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class BoomBossListener implements Listener {

    private final BoomBoss boomBoss;

    public BoomBossListener(BoomBoss boomBoss) {
        this.boomBoss = boomBoss;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        // Boss death
        if (event.getEntity().getUniqueId().equals(boomBoss.getBossUUID())) {
            boomBoss.onBossDeath();
            return;
        }

        // Minion death
        if (BoomBoss.isMinionEntity(event.getEntity())) {
            boomBoss.onMinionDeath(event.getEntity());
        }
    }
}

