package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class AllExplodeOnDeathEffect implements BoomEffect {

    String id = "all_explode_on_death";
    boolean active = false;

    @Override
    public void activate() {
        Boomba.getEventManager().activateEffectListeners(id);
        active = true;
    }

    @Override
    public void deactivate() {
        Boomba.getEventManager().deactivateEffectListeners(id);
        active = false;
    }

    @Override
    public String getInfo() {
        return "All explode on death: All entities will explode upon death.";
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
