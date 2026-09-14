package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class EndermanExplosiveTeleportEffect implements BoomEffect {

    String id = "enderman_explosive_teleport";
    boolean active = false;

    @Override
    public void activate() {
        if (active) return; // Prevent double activation
        Boomba.getEventManager().activateEffectListeners(id);
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return; // Prevent double deactivation
        Boomba.getEventManager().deactivateEffectListeners(id);
        active = false;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
