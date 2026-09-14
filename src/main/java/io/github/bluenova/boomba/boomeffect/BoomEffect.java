package io.github.bluenova.boomba.boomeffect;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BoomEffect {

    void activate();

    void deactivate();

    String getInfo();

    boolean isActive();


}