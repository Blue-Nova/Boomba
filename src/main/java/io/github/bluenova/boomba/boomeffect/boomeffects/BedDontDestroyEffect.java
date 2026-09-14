package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class BedDontDestroyEffect implements BoomEffect {

    String id = "beds_dont_destroy";
    boolean active = false;

    @Override
    public void activate() {
        Boomba.getEventManager().activateEffectListeners(id); // Activate listeners for this effect
        active = true;
    }

    @Override
    public void deactivate() {
        Boomba.getEventManager().deactivateEffectListeners(id); // Deactivate listeners for this effect
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
