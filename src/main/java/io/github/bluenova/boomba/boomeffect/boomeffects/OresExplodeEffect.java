package io.github.bluenova.boomba.boomeffect.boomeffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class OresExplodeEffect implements BoomEffect {

    String id = "ores_explode";
    boolean active = false;

    @Override
    public void activate() {
        if (active) return;
        active = true;

        Boomba.getEventManager().activateEffectListeners(id);
    }

    @Override
    public void deactivate() {
        if (!active) return;
        active = false;

        Boomba.getEventManager().deactivateEffectListeners(id);
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
