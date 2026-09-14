package io.github.bluenova.boomba.boomeffect;

import io.github.bluenova.boomba.Boomba;
import io.github.bluenova.boomba.UI.UIManager;
import io.github.bluenova.boomba.boomeffect.boomeffects.*;
import io.github.bluenova.boomba.boomeffect.boomeffects.TNTShowerEffect.TNTShowerEffect;
import io.github.bluenova.boomba.boomeffect.boomeffects.itemEffects.ExplosiveSackEffect;
import io.github.bluenova.boomba.boomeffect.boomeffects.itemEffects.UpgradedArmorEffect;
import io.github.bluenova.boomba.config.ConfigManager;
import io.github.bluenova.boomba.game.BoomBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BoomEffectsManager {

    private final String log_prefix = "[BoomEffectManager]";
    private final HashMap<String, BoomEffect> boomEffects;
    private final HashMap<Player , ArrayList<ItemStack>> playerHeldDrops = new HashMap<>();

    private final ArrayList<Player> playersInShowers = new ArrayList<>();

    private int globalDifficulty = 0;
    private final int maxDifficulty = ConfigManager.get_max_difficulty();
    private Location bastionLocation = null;
    private boolean bastionExists = false;

    private final int bastion_base_range = ConfigManager.get_bastion_base_range();
    private final int bastion_max_range = ConfigManager.get_bastion_max_range();
    private int bastion_range = bastion_base_range;

    private final int bastion_upgrade_amount = ConfigManager.get_bastion_upgrade_amount();

    private final HashMap<Material, Integer> bastionUpgradeMap = new HashMap<>();
    private final HashMap<Material, Integer> bastionUnlockedUpgrades = new HashMap<>();

    public BoomEffectsManager() {
        this.boomEffects = new HashMap<>();
        registerBoomEffects();
        applyConfigDefaults();
    }

    private void registerBoomEffects() {
        boomEffects.put("all_explode_on_death", new AllExplodeOnDeathEffect());
        boomEffects.put("creepers_high_knockback", new CreepersHighKnockbackEffect());
        boomEffects.put("phantoms_suicide_bombs", new PhantomsSuicideBombsEffect());
        boomEffects.put("chance_drop_tnt" , new ChanceDropTNTEffect());
        boomEffects.put("fall_damage_explode", new FallDamageExplodeEffect());
        boomEffects.put("enderman_explosive_teleport", new EndermanExplosiveTeleportEffect());
        boomEffects.put("explosive_punch", new ExplosivePunchEffect());
        boomEffects.put("tag_bomb", new TagBombEffect());
        boomEffects.put("tnt_shower", new TNTShowerEffect());
        boomEffects.put("flint_and_steel_hot_potato", new FlintAndSteelExplodeEffect());
        boomEffects.put("refill_hunger" , new RefillHungerEffect());
        boomEffects.put("bastion", new BastionEffect());
        boomEffects.put("keep_hotbar", new KeepHotbarEffect());
        boomEffects.put("creepers_lay_eggs", new CreepersLayEggsEffect());
        boomEffects.put("explosive_sack", new ExplosiveSackEffect());
        boomEffects.put("mine_field", new MineFieldEffect());
        boomEffects.put("ores_explode", new OresExplodeEffect());
        boomEffects.put("endermen_wave", new EnderMenWaveEffect());
        boomEffects.put("upgraded_armor", new UpgradedArmorEffect());
        boomEffects.put("ultimate_boomba", new UltimateBoombaEffect());
        registerBastionUpgradeMap();
    }

    // Apply the boolean defaults from ConfigManager to each registered effect
    private void applyConfigDefaults() {
        setActive("all_explode_on_death", ConfigManager.isAllExplodeOnDeathActiveByDefault());
        setActive("creepers_high_knockback", ConfigManager.isCreepersHighKnockbackActiveByDefault());
        setActive("phantoms_suicide_bombs", ConfigManager.isPhantomsSuicideBombersActiveByDefault());
        setActive("chance_drop_tnt", ConfigManager.isChanceDropTntActiveByDefault());
        setActive("fall_damage_explode", ConfigManager.isFallDamageExplodeActiveByDefault());
        setActive("enderman_explosive_teleport", ConfigManager.isEndermanExplosiveTeleportActiveByDefault());
        setActive("explosive_punch", ConfigManager.isExplosivePunchActiveByDefault());
        setActive("tag_bomb", ConfigManager.isTagBombActiveByDefault());
        setActive("tnt_shower", ConfigManager.isTntShowerActiveByDefault());
        setActive("flint_and_steel_hot_potato", ConfigManager.isFlintAndSteelExplodeActiveByDefault());
        setActive("refill_hunger", ConfigManager.isRefillHungerActiveByDefault());
        setActive("bastion", ConfigManager.isBastionActiveByDefault());
        setActive("keep_hotbar", ConfigManager.isKeepHotBarActiveByDefault());
        setActive("creepers_lay_eggs", ConfigManager.isCreepersLayEggsActiveByDefault());
        setActive("mine_field", ConfigManager.isMineFieldActiveByDefault());
        setActive("ores_explode", ConfigManager.isOresExplodeActiveByDefault());
        setActive("endermen_wave", ConfigManager.isEndermenWaveActiveByDefault());
        setActive("upgraded_armor", ConfigManager.isUpgradedArmorActiveByDefault());
        setActive("explosive_sack", ConfigManager.isExplosiveSackActiveByDefault());
        setActive("ultimate_boomba", false);
    }

    private void registerBastionUpgradeMap(){
        bastionUpgradeMap.put(Material.DIRT, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.SAND, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.COBBLESTONE, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.GRAVEL, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.COBBLED_DEEPSLATE, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.DIORITE, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.GRANITE, bastion_upgrade_amount);
        bastionUpgradeMap.put(Material.ANDESITE, bastion_upgrade_amount);

        bastionUnlockedUpgrades.put(Material.DIRT, 0);
        bastionUnlockedUpgrades.put(Material.SAND, 0);
        bastionUnlockedUpgrades.put(Material.COBBLESTONE, 0);
        bastionUnlockedUpgrades.put(Material.GRAVEL, 0);
        bastionUnlockedUpgrades.put(Material.COBBLED_DEEPSLATE, 0);
        bastionUnlockedUpgrades.put(Material.DIORITE, 0);
        bastionUnlockedUpgrades.put(Material.GRANITE, 0);
        bastionUnlockedUpgrades.put(Material.ANDESITE, 0);
    }

    private void setActive(String id, boolean active) {
        BoomEffect effect = boomEffects.get(id);
        if (effect == null) return;
        if (active) effect.activate();
        else effect.deactivate();
    }

    public void scaleDifficulty(int amount, Player player, boolean announce){
        if ((globalDifficulty + amount) > maxDifficulty) {
            player.sendActionBar(UIManager.mini("Not allowed to cross max level of " + maxDifficulty + "!"));
            return;
        }
        globalDifficulty += amount;
        if (announce)
            UIManager.announceDifficulty(globalDifficulty);
        else
            UIManager.confirmDifficulty(player);
    }

    public void scaleDifficulty(int amount, boolean announce){
        if ((globalDifficulty + amount) > maxDifficulty) {
            return;
        }
        globalDifficulty += amount;
        if (announce) UIManager.announceDifficulty(globalDifficulty);
    }

    public void setDifficulty(int amount, Player player, boolean announce) {
        if ((globalDifficulty + amount) > maxDifficulty) {
            player.sendActionBar(UIManager.mini("Not allowed to cross max level of " + maxDifficulty + "!"));
            return;
        }
        globalDifficulty = amount;
        if (announce)
            UIManager.announceDifficulty(globalDifficulty);
        else
            UIManager.confirmDifficulty(player);
    }

    public ArrayList<String> getAllActiveEffectIds() {
        ArrayList<String> activeEffects = new ArrayList<>();
        for (Map.Entry<String,BoomEffect> entry : boomEffects.entrySet()) {
            if (entry.getValue().isActive()) {
                activeEffects.add(entry.getKey());
            }
        }
        return activeEffects;
    }

    public ArrayList<String> getAllDisabledEffectIds() {
        ArrayList<String> disabledEffects = new ArrayList<>();
        for (Map.Entry<String,BoomEffect> entry : boomEffects.entrySet()) {
            if (!entry.getValue().isActive()) {
                disabledEffects.add(entry.getKey());
            }
        }
        return disabledEffects;
    }

    // boolean to tell commands how to handle
    public boolean disableEffect(String arg) {
        if (boomEffects.containsKey(arg)) {
            boomEffects.get(arg).deactivate();
            return true;
        }
        return false;
    }

    public boolean enableEffect(String arg) {
        if (boomEffects.containsKey(arg)) {
            boomEffects.get(arg).activate();
            return true;
        }
        return false;
    }

    // API used by listener: ask the fall effect to handle this player's fall (returns true if it was handled/exploded)
    public boolean handlePlayerFall(Player player, float fallDistance) {
        if (boomEffects.containsKey("fall_damage_explode") && boomEffects.get("fall_damage_explode").isActive()) {
            FallDamageExplodeEffect effect = (FallDamageExplodeEffect) boomEffects.get("fall_damage_explode");
            return effect.handlePlayerFall(player, fallDistance);
        }
        return false;
    }

    public void handleExplosivePunchPlayerDamaged(Player player, double damage) {
        if (boomEffects.containsKey("explosive_punch") && boomEffects.get("explosive_punch").isActive()) {
            ExplosivePunchEffect effect = (ExplosivePunchEffect) boomEffects.get("explosive_punch");
            if (!effect.isActive()) return;
            effect.handlePlayerDamage(player, damage);
        }
    }

    public void handleExplosivePunchPlayerPunch(Player player, LivingEntity target) {
        if (boomEffects.containsKey("explosive_punch") && boomEffects.get("explosive_punch").isActive()) {
            ExplosivePunchEffect effect = (ExplosivePunchEffect) boomEffects.get("explosive_punch");
            if (!effect.isActive()) return;
            effect.handlePlayerPunch(player, target);
        }
    }

    public void handleTagBombAttach(Player player, Entity rightClicked) {
        if (boomEffects.containsKey("tag_bomb") && boomEffects.get("tag_bomb").isActive()) {
            TagBombEffect effect = (TagBombEffect) boomEffects.get("tag_bomb");
            if (!effect.isActive()) return;
            effect.handleAttach(player, rightClicked);
        }
    }

    public void handleTagBombPlace(Player player, Block block, ItemStack itemUsed) {
        if (boomEffects.containsKey("tag_bomb") && boomEffects.get("tag_bomb").isActive()) {
            TagBombEffect effect = (TagBombEffect) boomEffects.get("tag_bomb");
            if (!effect.isActive()) return;
            effect.handlePlace(player, block, itemUsed);
        }
    }

    // returns true if the explosion was handled (and the vanilla one should be cancelled)
    public boolean handleTagBombExplosion(Entity entity) {
        if (boomEffects.containsKey("tag_bomb") && boomEffects.get("tag_bomb").isActive()) {
            TagBombEffect effect = (TagBombEffect) boomEffects.get("tag_bomb");
            if (!effect.isActive()) return false;
            return effect.handleExplosionPrime(entity);
        }
        return false;
    }

    public void handleUltimateBoombaPlace(Player player, Block block, ItemStack itemUsed) {
        if (boomEffects.containsKey("ultimate_boomba") && boomEffects.get("ultimate_boomba").isActive()) {
            UltimateBoombaEffect effect = (UltimateBoombaEffect) boomEffects.get("ultimate_boomba");
            if (!effect.isActive()) return;
            effect.handlePlace(player, block, itemUsed);
        }
    }

    public void handleUltimateBoombaPrimedEntitySpawn(Entity entity) {
        if (boomEffects.containsKey("ultimate_boomba") && boomEffects.get("ultimate_boomba").isActive()) {
            UltimateBoombaEffect effect = (UltimateBoombaEffect) boomEffects.get("ultimate_boomba");
            if (!effect.isActive()) return;
            effect.handlePrimedEntitySpawn(entity);
        }
    }

    public boolean handleUltimateBoombaExplosion(Entity entity) {
        if (boomEffects.containsKey("ultimate_boomba") && boomEffects.get("ultimate_boomba").isActive()) {
            Boomba.getInstance().log(log_prefix, "Handling ultimate boomba explosion for entity " + entity.getUniqueId());
            UltimateBoombaEffect effect = (UltimateBoombaEffect) boomEffects.get("ultimate_boomba");
            if (!effect.isActive()) return false;
            Boomba.getInstance().log(log_prefix, "Effect is active, calling handleExplosion");
            return effect.handleExplosion(entity);
        }
        return false;
    }

    public void addPlayerToShowers(Player player) {
        if (!playersInShowers.contains(player)) {
            playersInShowers.add(player);
        }
    }

    public void removePlayerFromShowers(Player player) {
        playersInShowers.remove(player);
    }

    public boolean isPlayerInShowers(Player player) {
        return playersInShowers.contains(player);
    }

    public ArrayList<Player> getPlayersNotInShowers() {
        ArrayList<Player> notInShowers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isPlayerInShowers(player)) {
                notInShowers.add(player);
            }
        }
        return notInShowers;
    }

    public Player getPlayerForShower() {
        // get random player that is not in a shower
        ArrayList<Player> notInShowers = getPlayersNotInShowers();
        if (notInShowers.isEmpty()) return null;
        int randomIndex = (int) (Math.random() * notInShowers.size());
        return notInShowers.get(randomIndex);
    }

    // API for HOT POTATO
    public void startHotPotato(Player player, ItemStack item, boolean replace_hand) {
        if (boomEffects.containsKey("flint_and_steel_hot_potato") && boomEffects.get("flint_and_steel_hot_potato").isActive()) {
            FlintAndSteelExplodeEffect effect = (FlintAndSteelExplodeEffect) boomEffects.get("flint_and_steel_hot_potato");
            if (!effect.isActive()) return;
            effect.startHotPotato(player, replace_hand);

        }
    }

    public boolean isItemHotPotato(ItemStack item) {
        if (boomEffects.containsKey("flint_and_steel_hot_potato") && boomEffects.get("flint_and_steel_hot_potato").isActive()) {
            FlintAndSteelExplodeEffect effect = (FlintAndSteelExplodeEffect) boomEffects.get("flint_and_steel_hot_potato");
            if (!effect.isActive()) return false;
            return effect.isItemHotPotato(item);
        }
        return false;
    }

    public boolean isPlayerHoldingHotPotato(Player player) {
        if (boomEffects.containsKey("flint_and_steel_hot_potato") && boomEffects.get("flint_and_steel_hot_potato").isActive()) {
            FlintAndSteelExplodeEffect effect = (FlintAndSteelExplodeEffect) boomEffects.get("flint_and_steel_hot_potato");
            if (!effect.isActive()) return false;
            return effect.isPlayerHoldingHotPotato(player);
        }
        return false;
    }

    public void transferHotPotato(Player player, Player entity) {
        if (boomEffects.containsKey("flint_and_steel_hot_potato") && boomEffects.get("flint_and_steel_hot_potato").isActive()) {
            FlintAndSteelExplodeEffect effect = (FlintAndSteelExplodeEffect) boomEffects.get("flint_and_steel_hot_potato");
            if (!effect.isActive()) return;
            effect.switchHotPotato(player, entity);
        }
    }

    public void playerAteHotPotato(Player player) {
        if (boomEffects.containsKey("flint_and_steel_hot_potato") && boomEffects.get("flint_and_steel_hot_potato").isActive()) {
            FlintAndSteelExplodeEffect effect = (FlintAndSteelExplodeEffect) boomEffects.get("flint_and_steel_hot_potato");
            if (!effect.isActive()) return;
            effect.playerAteHotPotato(player);
        }
    }

    // API FOR EFFECTS
    public boolean enableNextEffect(Player player) {
        for (Map.Entry<String, BoomEffect> entry : boomEffects.entrySet()) {
            if (!entry.getValue().isActive()) {
                UIManager.showNextEffectEnabled(player, entry.getKey());
                entry.getValue().activate();
                return true;
            }
        }
        return false;
    }

    public void enableNextEffect() {
        for (Map.Entry<String, BoomEffect> entry : boomEffects.entrySet()) {
            if (!entry.getValue().isActive()) {
                entry.getValue().activate();
                UIManager.showNextEffectEnabled(entry.getKey());
                return;
            }
        }
    }

    public int getCurrentDifficulty() {
        return globalDifficulty;
    }

    public void holdPlayerDrops(Player player, ArrayList<ItemStack> drops) {
        playerHeldDrops.putIfAbsent(player, drops);
    }

    public void dropPlayerHeldDrops(Player player, Location loc) {
        if (playerHeldDrops.containsKey(player)) {
            ArrayList<ItemStack> drops = playerHeldDrops.get(player);
            for (ItemStack drop : drops) {
                player.getWorld().dropItemNaturally(loc, drop);
            }
            playerHeldDrops.remove(player);
        }
    }

    public int getBastionRange(){
        return bastion_range;
    }

    public boolean isBastionExists() {
        return bastionExists;
    }

    public void placeBastion(Location loc){
        if (bastionExists) return;
        UIManager.showBastionPlaced(Boomba.getAllPlayers(), loc);
        bastionExists = true;
        bastionLocation = loc;
        Bukkit.getScheduler().runTaskLater(Boomba.getInstance(), () -> {
            Boomba.getWaveManager().start();
            },20*5);
    }

    public void setBastionBlock(Location loc, Player p) {
        if (bastionExists) return;
        UIManager.updateBastionBlock(loc, p);
        bastionLocation = loc;
        bastionExists = true;
    }

    public void attemptUpgradeBastion(Player player, ItemStack itemInHand) {
        // if bastion is at max upgrade, then cancel and inform the player
        if (bastion_range >= bastion_max_range){
            UIManager.showBastionMaxLevel(player);
        }

        Material material = itemInHand.getType();
        int amount = itemInHand.getAmount();
        Boomba.getInstance().log(log_prefix, "Entered attemptUpgradeBastion with material -> " + material + " and amount -> " + amount);

        Integer upgradeAmountObj = bastionUpgradeMap.get(material);
        if (upgradeAmountObj == null) {
            // not an upgrade material
            return;
        }
        int needed = upgradeAmountObj; // remaining needed to next upgrade (e.g. 573)
        int provided = amount;

        int upgradesToApply = 0;
        int newNeeded;

        if (provided < needed) {
            // not enough to upgrade even once
            newNeeded = needed - provided;
            if (amount >= 0) {
                UIManager.showCurrentBastionUpgradeProgress(material, newNeeded, bastion_upgrade_amount);
            }
        } else {
            // enough for at least one upgrade
            provided -= needed; // consume what's needed for the first upgrade
            upgradesToApply = 1 + (provided / bastion_upgrade_amount); // additional full upgrades from remaining blocks
            int leftoverAfterUpgrades = provided % bastion_upgrade_amount;
            newNeeded = bastion_upgrade_amount - leftoverAfterUpgrades; // remaining needed for the next upgrade (573 if leftoverAfterUpgrades == 0)
            // apply upgrades
            for (int i = 0; i < upgradesToApply; i++) {
                upgradeBastionWithMaterial(material);
            }
        }

        bastionUpgradeMap.put(material, newNeeded);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }

    private void upgradeBastionWithMaterial(Material material) {
        bastionUnlockedUpgrades.forEach((upgradeMaterial,level)->{
            if (material.equals(upgradeMaterial)){
                bastionUnlockedUpgrades.put(upgradeMaterial, level+1);
                recalculateBastionLevel();
                UIManager.showUpgradeBastion(bastion_range);
            }
        });
    }

    private void recalculateBastionLevel() {
        AtomicInteger rangeFromUpgrades = new AtomicInteger();
        bastionUnlockedUpgrades.forEach((material,level)->{
            rangeFromUpgrades.set(rangeFromUpgrades.get() + level);
        });
        bastion_range = bastion_base_range + rangeFromUpgrades.get();
    }


    public void checkIfMined(Block block, Player player, boolean explodeInstantly) {
        if (boomEffects.containsKey("mine_field") && boomEffects.get("mine_field").isActive()) {
            MineFieldEffect mine_field_effect = (MineFieldEffect) boomEffects.get("mine_field");
            mine_field_effect.checkIfMined(block, player, explodeInstantly);

        }

    }

    public void checkMinedPlayer(Player player) {
        if (boomEffects.containsKey("mine_field") && boomEffects.get("mine_field").isActive()) {
            MineFieldEffect mine_field_effect = (MineFieldEffect) boomEffects.get("mine_field");
            mine_field_effect.checkMinedPlayer(player);
        }
    }

    public Location getBastionSpawnLocation() {
        if (!bastionExists) return null;
        if (bastionLocation == null) {
            return null;
        }
        // get the location of the bastion block
        return bastionLocation.clone().add(0.5,1,0.5);
    }

    public void spawnBoss() {
        Location spawnLocation = getBastionSpawnLocation();
        if (spawnLocation == null) {
            Boomba.getInstance().getLogger().warning("Attempted to spawn boss but bastion spawn location was null!");
            return;
        }
        BoomBoss boss = new BoomBoss(spawnLocation);
    }

    public int getMaxDifficulty() {
        return maxDifficulty;
    }


}
