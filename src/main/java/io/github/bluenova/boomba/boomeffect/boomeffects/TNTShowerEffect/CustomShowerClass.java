package io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.entity.Player;

public class CustomShowerClass implements CustomShower {

     public CustomShowerClass() {
     }

    @Override
    public void startShower(Player player, Runnable onComplete) {
         if (onComplete != null) onComplete.run();
    }

    @Override
    public void stopShower() {
    }

    protected void runShowerTask(Runnable task) {
        // helper to run a task on the main server thread
        Boomba.getInstance().getServer().getScheduler().runTask(Boomba.getInstance(), task);
    }
}
