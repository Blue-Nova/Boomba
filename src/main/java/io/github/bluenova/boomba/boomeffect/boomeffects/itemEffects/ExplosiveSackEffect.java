package io.github.bluenova.boomba.boomeffect.boomeffects.itemEffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class ExplosiveSackEffect implements BoomEffect {

    String id = "explosive_sack";
    boolean active = false;

    @Override
    public void activate() {
        if (active) return;
        Boomba.getItemManager().enableItem(id);
        Boomba.getEventManager().activateEffectListeners(id);
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) return;
        Boomba.getItemManager().disableItem(id);
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
