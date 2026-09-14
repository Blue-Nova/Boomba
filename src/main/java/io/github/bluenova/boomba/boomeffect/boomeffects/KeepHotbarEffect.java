package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class KeepHotbarEffect implements BoomEffect {

    String id = "keep_hotbar";
    boolean active = false;

    @Override
    public void activate() {
        // Activate listeners (if you have any) for this effect
        if (active) return; // Prevent double activation
        Boomba.getEventManager().activateEffectListeners(id);
        active = true;
    }

    @Override
    public void deactivate() {
        // Deactivate listeners for this effect
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
