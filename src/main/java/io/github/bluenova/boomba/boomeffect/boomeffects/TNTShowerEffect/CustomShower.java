package io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect;

import org.bukkit.entity.Player;

public interface CustomShower {

    /**
     * Start the shower for the given player. When the shower finishes it MUST call the provided onComplete Runnable
     * (this allows the caller to chain showers without blocking the main thread).
     * @param player the player to target
     * @param onComplete callback to be invoked when this shower is finished
     */
    void startShower(Player player, Runnable onComplete);

    /**
     * Stop the shower early. Implementations should ensure onComplete is invoked if appropriate.
     */
    void stopShower();

}
