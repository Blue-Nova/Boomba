package io.github.bluenova.boomba.events;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.events.events.AllExplodeOnDeath.AllExplodeOnDeathEvent;
import io.github.bluenova.boomba.events.events.AllExplodeOnDeath.PlayerDamageExplosionEvent;
import io.github.bluenova.boomba.events.events.Bastion.*;
import io.github.bluenova.boomba.events.events.BedsDontDestroy.BedDestroyEvent;
import io.github.bluenova.boomba.events.events.CreepersHighKnockback.CreeperDamagePlayerEvent;
import io.github.bluenova.boomba.events.events.CreepersHighKnockback.CreeperKillCreeperEvent;
import io.github.bluenova.boomba.events.events.CreepersHighKnockback.PlayerKillCreeperEvent;
import io.github.bluenova.boomba.events.events.CreepersLayEggs.CreeperSpawnEvent;
import io.github.bluenova.boomba.events.events.EndermanExplosiveTeleport.EndermanPlaceBlockEvent;
import io.github.bluenova.boomba.events.events.EndermanExplosiveTeleport.EndermanTeleportEvent;
import io.github.bluenova.boomba.events.events.ExplosivePunch.PlayerDamagedEvent;
import io.github.bluenova.boomba.events.events.ExplosivePunch.PlayerPunchEntityEvent;
import io.github.bluenova.boomba.events.events.FallDamageExplode.PlayerFallDamageEvent;
import io.github.bluenova.boomba.events.events.FlintAndSteelExplode.FlintAndSteelUseEvent;
import io.github.bluenova.boomba.events.events.FlintAndSteelExplode.HotPotatoDropEvent;
import io.github.bluenova.boomba.events.events.FlintAndSteelExplode.PlayerEatHotPotatoEvent;
import io.github.bluenova.boomba.events.events.FlintAndSteelExplode.PlayerRightClickPlayerEvent;
import io.github.bluenova.boomba.events.events.KeepHotbar.PlayerDieEvent;
import io.github.bluenova.boomba.events.events.MineFieldEffect.OnMineBreakEvent;
import io.github.bluenova.boomba.events.events.MineFieldEffect.OnPlayerStepOnMineEvent;
import io.github.bluenova.boomba.events.events.OresExplode.PlayerMineOreEvent;
import io.github.bluenova.boomba.events.events.PhantomSuicideBombers.PhantomDamagePlayerEvent;
import io.github.bluenova.boomba.events.events.PhantomSuicideBombers.PlayerKillPhantomEvent;
import io.github.bluenova.boomba.events.events.TagBomb.TagBombAttachEvent;
import io.github.bluenova.boomba.events.events.TagBomb.TagBombPlaceEvent;
import io.github.bluenova.boomba.events.events.UltimateBoomba.UltimateBoombaExplodeEvent;
import io.github.bluenova.boomba.events.events.UltimateBoomba.UltimateBoombaPlaceEvent;
import io.github.bluenova.boomba.events.events.UltimateBoomba.UltimateBoombaSpawnEvent;
import io.github.bluenova.boomba.events.events.items.ExplosiveSack.ExplosiveSackReloadEvent;
import io.github.bluenova.boomba.events.events.items.ExplosiveSack.ExplosiveSackUseEvent;
import io.github.bluenova.boomba.events.events.items.UpgradedArmor.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {

    private static final HashMap<String, ArrayList<Listener>> EffectOnListeners = new HashMap<>();

    public EventManager() {

        registerMap();

    }

    private void registerMap() {

        // all explode on death
        ArrayList<Listener> allExplodeOnDeathListeners = new ArrayList<>();
        allExplodeOnDeathListeners.add(new AllExplodeOnDeathEvent());
        allExplodeOnDeathListeners.add(new PlayerDamageExplosionEvent());
        EffectOnListeners.put("all_explode_on_death", allExplodeOnDeathListeners);

        // creepers high knockback
        ArrayList<Listener> creepersHighKnockback = new ArrayList<>();
        creepersHighKnockback.add(new CreeperDamagePlayerEvent());
        creepersHighKnockback.add(new PlayerKillCreeperEvent());
        creepersHighKnockback.add(new CreeperKillCreeperEvent());
        EffectOnListeners.put("creepers_high_knockback", creepersHighKnockback);

        // phantoms suicide bombs
        ArrayList<Listener> phantomsSuicideBombs = new ArrayList<>();
        phantomsSuicideBombs.add(new PhantomDamagePlayerEvent());
        phantomsSuicideBombs.add(new PlayerKillPhantomEvent());
        EffectOnListeners.put("phantoms_suicide_bombs", phantomsSuicideBombs);

        // fall damage explosion
        ArrayList<Listener> fallDamageExplode = new ArrayList<>();
        fallDamageExplode.add(new PlayerFallDamageEvent());
        EffectOnListeners.put("fall_damage_explode", fallDamageExplode);

        // enderman explosive teleport
        ArrayList<Listener> endermanExplosiveTeleport = new ArrayList<>();
        endermanExplosiveTeleport.add(new EndermanTeleportEvent());
        endermanExplosiveTeleport.add(new EndermanPlaceBlockEvent());
        EffectOnListeners.put("enderman_explosive_teleport", endermanExplosiveTeleport);

        // Explosive Punch
        ArrayList<Listener> explosivePunch = new ArrayList<>();
        explosivePunch.add(new PlayerDamagedEvent());
        explosivePunch.add(new PlayerPunchEntityEvent());
        EffectOnListeners.put("explosive_punch", explosivePunch);

        // Tag Bomb
        ArrayList<Listener> tagBomb = new ArrayList<>();
        tagBomb.add(new TagBombAttachEvent());
        tagBomb.add(new TagBombPlaceEvent());
        EffectOnListeners.put("tag_bomb", tagBomb);

        // Flint and Steel Explode
        ArrayList<Listener> flintAndSteelExplode = new ArrayList<>();
        flintAndSteelExplode.add(new FlintAndSteelUseEvent());
        flintAndSteelExplode.add(new HotPotatoDropEvent());
        flintAndSteelExplode.add(new PlayerRightClickPlayerEvent());
        flintAndSteelExplode.add(new PlayerEatHotPotatoEvent());
        EffectOnListeners.put("flint_and_steel_hot_potato", flintAndSteelExplode);

        // Beds Don't Destroy
        ArrayList<Listener> bedDontDestroy = new ArrayList<>();
        bedDontDestroy.add(new BedDestroyEvent());
        EffectOnListeners.put("beds_dont_destroy", bedDontDestroy);

        // Bastion
        ArrayList<Listener> bastion = new ArrayList<>();
        bastion.add(new PlaceBastionEvent());
        bastion.add(new BreakBastionEvent());
        bastion.add(new BastionProtectBlockEvent());
        bastion.add(new BastionProtectEntityEvent());
        bastion.add(new PlayerRightClickBastionEvent());
        bastion.add(new PlayerRespawnAtBastionEvent());
        EffectOnListeners.put("bastion", bastion);

        // Keep Hotbar
        ArrayList<Listener> keepHotbar = new ArrayList<>();
        keepHotbar.add(new PlayerDieEvent());
        EffectOnListeners.put("keep_hotbar", keepHotbar);

        // creepers lay eggs
        ArrayList<Listener> creepersLayEggs = new ArrayList<>();
        creepersLayEggs.add(new CreeperSpawnEvent());
        EffectOnListeners.put("creepers_lay_eggs", creepersLayEggs);

        // explosive sack
        ArrayList<Listener> explosiveSack = new ArrayList<>();
        explosiveSack.add(new ExplosiveSackUseEvent());
        explosiveSack.add(new ExplosiveSackReloadEvent());
        EffectOnListeners.put("explosive_sack", explosiveSack);

        ArrayList<Listener> mineField = new ArrayList<>();
        mineField.add(new OnPlayerStepOnMineEvent());
        mineField.add(new OnMineBreakEvent());
        EffectOnListeners.put("mine_field", mineField);

        ArrayList<Listener> oresExplode = new ArrayList<>();
        oresExplode.add(new PlayerMineOreEvent());
        EffectOnListeners.put("ores_explode", oresExplode);

        ArrayList<Listener> upgradedArmor = new ArrayList<>();
        upgradedArmor.add(new WoolShoesCraftEvent());
        upgradedArmor.add(new BlastChestplateCraftEvent());
        upgradedArmor.add(new BlastLeggingsCraftEvent());
        upgradedArmor.add(new ProtectionHelmetCraftEvent());

        // tools
        upgradedArmor.add(new EfficiencyPickaxeCraftEvent());
        upgradedArmor.add(new BoombaSwordCraftEvent());
        upgradedArmor.add(new BoomBowCraftEvent());
        EffectOnListeners.put("upgraded_armor", upgradedArmor);

        ArrayList<Listener> ultimateBoomba = new ArrayList<>();
        ultimateBoomba.add(new UltimateBoombaPlaceEvent());
        ultimateBoomba.add(new UltimateBoombaSpawnEvent());
        ultimateBoomba.add(new UltimateBoombaExplodeEvent());
        EffectOnListeners.put("ultimate_boomba", ultimateBoomba);
    }

    public void activateEffectListeners(String effectId) {
        Boomba.getInstance().getLogger().info("Activating listeners for effect: " + effectId);
        if (!EffectOnListeners.containsKey(effectId)) {
            Boomba.getInstance().getLogger().warning("No listeners found for effect: " + effectId);
            return;
        }
        for (Listener listener : EffectOnListeners.get(effectId)) {
            Boomba.getInstance().getServer().getPluginManager().registerEvents(listener, Boomba.getInstance());
        }
    }

    public void deactivateEffectListeners(String effectId) {
        for (Listener listener : EffectOnListeners.get(effectId)) {
            HandlerList.unregisterAll(listener);
        }
    }

}
