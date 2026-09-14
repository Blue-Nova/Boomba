package io.github.bluenova.boomba.boomeffect.boomeffects.itemEffects;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.boomeffect.BoomEffect;

public class UpgradedArmorEffect implements BoomEffect {

    String id = "upgraded_armor";
    boolean active = false;

    @Override
    public void activate() {
        if (active) return;
        active = true;
        //Boomba.getItemManager().enableItem("wool_shoes");
        //Boomba.getItemManager().enableItem("blast_chestplate");
        Boomba.getEventManager().activateEffectListeners("upgraded_armor");
    }

    @Override
    public void deactivate() {
        if (!active) return;
        active = false;
        //Boomba.getItemManager().disableItem("wool_shoes");
        //Boomba.getItemManager().enableItem("blast_chestplate");
        Boomba.getEventManager().deactivateEffectListeners("upgraded_armor");
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
