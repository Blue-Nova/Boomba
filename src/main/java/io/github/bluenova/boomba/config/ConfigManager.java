package io.github.bluenova.boomba.config;

import io.github.bluenova.boomba.Boomba;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private static File file;
    private static YamlConfiguration config;

    //private static boolean BED_DONT_DESTROY_ACTIVE_BY_DEFAULT;

    // General Config
    private static int WAVE_DURATION_SECONDS;
    private static int MAX_DIFFICULTY;


    // effects that are enabled by default
    private static boolean ALL_EXPLODE_ON_DEATH_ACTIVE_BY_DEFAULT;
    private static float ALL_EXPLODE_ON_DEATH_HEARTS_PER_EXPLOSION_POWER;
    private static int ALL_EXPLODE_ON_DEATH_DIFFICULTY_PER_EXPLOSION_POWER;

    // Chance Drop TNT
    private static boolean CHANCE_DROP_TNT_ACTIVE_BY_DEFAULT;
    private static int CHANCE_DROP_TNT_CHANCE_PERCENT;
    private static int CHANCE_DROP_TNT_CHECK_INTERVAL_SECONDS;

    // Creepers High Knockback
    private static boolean CREEPERS_HIGH_KNOCKBACK_ACTIVE_BY_DEFAULT;
    private static double CREEPERS_HIGH_KNOCKBACK_KNOCKBACK_MULTIPLIER;

    // Enderman Explosive Teleport
    private static boolean ENDERMAN_EXPLOSIVE_TELEPORT_ACTIVE_BY_DEFAULT;
    private static float ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER;
    private static float ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER_PER_DIFFICULTY;

    // Explosive Punch
    private static boolean EXPLOSIVE_PUNCH_ACTIVE_BY_DEFAULT;
    private static int EXPLOSIVE_PUNCH_MINIMUM_DAMAGE_THRESHOLD;

    // Fall Damage Explode
    private static boolean FALL_DAMAGE_EXPLODE_ACTIVE_BY_DEFAULT;
    private static int FALL_DAMAGE_EXPLODE_COOLDOWN;
    private static int FALL_DAMAGE_EXPLODE_POWER_PER_FALL_DISTANCE;
    private static int FALL_DAMAGE_EXPLODE_EXPLOSION_COUNT_PER_EXPLOSION_POWER;

    // Flint and Steel Explode
    private static boolean FLINT_AND_STEEL_EXPLODE_ACTIVE_BY_DEFAULT;

    // Phantoms Suicide Bombers
    private static boolean PHANTOMS_SUICIDE_BOMBERS_ACTIVE_BY_DEFAULT;
    private static float PHANTOMS_SUICIDE_BOMBERS_EXPLOSION_POWER;
    private static int PHANTOMS_SUICIDE_BOMBERS_ATTEMPT_COOLDOWN_SECONDS;
    private static int PHANTOMS_SUICIDE_BOMBERS_ADDED_PERCENTAGE_ON_FAIL;
    private static int PHANTOMS_SUICIDE_BOMBERS_CHANCE_PERCENTAGE;

    // Refill Hunger
    private static boolean REFILL_HUNGER_ACTIVE_BY_DEFAULT;
    private static int REFILL_HUNGER_COOLDOWN_SECONDS;
    private static int REFILL_HUNGER_AMOUNT;

    // Tag Bomb
    private static boolean TAG_BOMB_ACTIVE_BY_DEFAULT;

    // TNT Shower
    private static boolean TNT_SHOWER_ACTIVE_BY_DEFAULT;
    private static int TNT_SHOWER_CHANCE_PERCENTAGE;
    private static int TNT_SHOWER_COOLDOWN_SECONDS;
    private static int TNT_SHOWER_PERCENTAGE_INCREASE_ON_FAIL;
    // TNT Shower - Random Shower
    private static int TNT_SHOWER_RANDOM_SHOWER_TOTAL_SPAWN;
    private static int TNT_SHOWER_RANDOM_SHOWER_INTERVAL_TICKS;
    private static int TNT_SHOWER_RANDOM_SHOWER_HEIGHT_ABOVE_PLAYER;
    private static int TNT_SHOWER_RANDOM_SHOWER_RADIUS;
    private static int TNT_SHOWER_RANDOM_SHOWER_FUSE_TICKS;
    // TNT SHower - Popin Shower
    private static int TNT_SHOWER_POPIN_SHOWER_TOTAL_SPAWNS;
    private static int TNT_SHOWER_POPIN_SHOWER_INTERVAL_TICKS;
    private static int TNT_SHOWER_POPIN_SHOWER_FUSE_TICKS;
    private static int TNT_SHOWER_POPIN_SHOWER_SEATED_FUSE_TICKS;
    // TNT Shower - Sniper Shower
    private static int TNT_SHOWER_SNIPER_SHOWER_TOTAL_SPAWNS;
    private static int TNT_SHOWER_SNIPER_SHOWER_INTERVAL_TICKS;
    private static int TNT_SHOWER_SNIPER_SHOWER_HEIGHT_ABOVE_PLAYER;
    private static int TNT_SHOWER_SNIPER_SHOWER_RADIUS;
    private static int TNT_SHOWER_SNIPER_SHOWER_DELAY_BEFORE_LAUNCH_TICKS;
    private static int TNT_SHOWER_SNIPER_SHOWER_SPEED_PER_TICK;
    private static int TNT_SHOWER_SNIPER_SHOWER_EXPLOSION_MARGIN_TICKS;

    // Bastion
    private static boolean BASTION_ACTIVE_BY_DEFAULT;
    private static int BASTION_EXPLOSION_DAMAGE_REDUCTION_IN_RANGE;
    private static int BASTION_BASE_RANGE;
    private static int BASTION_MAX_RANGE;
    private static int BASTION_UPGRADE_AMOUNT;

    // Keep Hotbar
    private static boolean KEEP_HOTBAR_ACTIVE_BY_DEFAULT;

    // Creepers Lay Eggs
    private static boolean CREEPERS_LAY_EGGS_ACTIVE_BY_DEFAULT;
    private static int CREEPERS_LAY_EGGS_MIN_SECONDS;
    private static int CREEPERS_LAY_EGGS_MAX_SECONDS;

    // Mine Field
    private static boolean MINE_FIELD_ACTIVE_BY_DEFAULT;
    private static int MINE_FIELD_COOLDOWN_SECONDS;
    private static float MINE_FIELD_EXPLOSION_POWER;

    // Ores Explode
    private static boolean ORES_EXPLODE_ACTIVE_BY_DEFAULT;
    private static float ORES_EXPLODE_COAL_ORE;
    private static float ORES_EXPLODE_IRON_ORE;
    private static float ORES_EXPLODE_GOLD_ORE;
    private static float ORES_EXPLODE_DIAMOND_ORE;
    private static float ORES_EXPLODE_EMERALD_ORE;
    private static float ORES_EXPLODE_NETHER_QUARTZ_ORE;
    private static float ORES_EXPLODE_DEEPSLATE_COAL_ORE;
    private static float ORES_EXPLODE_DEEPSLATE_IRON_ORE;
    private static float ORES_EXPLODE_DEEPSLATE_GOLD_ORE;
    private static float ORES_EXPLODE_DEEPSLATE_DIAMOND_ORE;
    private static float ORES_EXPLODE_DEEPSLATE_EMERALD_ORE;

    // Enderman Wave
    private static boolean ENDERMAN_WAVE_ACTIVE_BY_DEFAULT;
    private static int ENDERMAN_WAVE_ATTEMPT_CHANCE;
    private static int ENDERMAN_WAVE_COOLDOWN_SECONDS;

    // Upgraded Armor
    private static boolean UPGRADED_ARMOR_ACTIVE_BY_DEFAULT;

    // global settings
    private static short DEFAULT_DIFFICULTY;

    public ConfigManager() {
        // Initialize your configuration manager here
    }

    public static void load() {
        file = new File(Boomba.getInstance().getDataFolder(), "config.yml");

        if (!file.exists())
            Boomba.getInstance().saveResource("config.yml", true);

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== General Configs =======

        WAVE_DURATION_SECONDS = config.getInt("wave_duration_seconds", 300);
        MAX_DIFFICULTY = config.getInt("max_difficulty", 12);


        // ===== active by default =====

        // All Explode on Death
        ALL_EXPLODE_ON_DEATH_ACTIVE_BY_DEFAULT = config.getBoolean("all_explode_on_death_active_by_default", true);
        ALL_EXPLODE_ON_DEATH_HEARTS_PER_EXPLOSION_POWER = (float) config.getDouble("all_explode_on_death_hearts_per_explosion_power", 2.0);
        ALL_EXPLODE_ON_DEATH_DIFFICULTY_PER_EXPLOSION_POWER = config.getInt("all_explode_on_death_difficulty_per_explosion_power", 1);

        // Creepers High Knockback
        CREEPERS_HIGH_KNOCKBACK_ACTIVE_BY_DEFAULT = config.getBoolean("creepers_high_knockback_active_by_default", true);
        CREEPERS_HIGH_KNOCKBACK_KNOCKBACK_MULTIPLIER = config.getDouble("creepers_high_knockback_knockback_multiplier", 2.0);

        // Enderman Explosive Teleport
        ENDERMAN_EXPLOSIVE_TELEPORT_ACTIVE_BY_DEFAULT = config.getBoolean("enderman_explosive_teleport_active_by_default", true);
        ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER = (float) config.getDouble("enderman_explosive_teleport_explosion_power", 5.0);
        ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER_PER_DIFFICULTY = (float) config.getDouble("enderman_explosive_teleport_explosion_power_per_difficulty", 0.2);

        // Explosive Punch
        EXPLOSIVE_PUNCH_ACTIVE_BY_DEFAULT = config.getBoolean("explosive_punch_active_by_default", true);
        EXPLOSIVE_PUNCH_MINIMUM_DAMAGE_THRESHOLD =  config.getInt("explosive_punch_minimum_damage_threshold", 6);

        // Fall Damage Explode
        FALL_DAMAGE_EXPLODE_ACTIVE_BY_DEFAULT = config.getBoolean("fall_damage_explode_active_by_default", true);
        FALL_DAMAGE_EXPLODE_COOLDOWN = config.getInt("fall_damage_explode_cooldown", 80);
        FALL_DAMAGE_EXPLODE_POWER_PER_FALL_DISTANCE = config.getInt("fall_damage_explode_power_per_fall_distance", 10);
        FALL_DAMAGE_EXPLODE_EXPLOSION_COUNT_PER_EXPLOSION_POWER = config.getInt("fall_damage_explode_explosion_count_per_explosion_power", 7);

        // Flint and Steel Explode
        FLINT_AND_STEEL_EXPLODE_ACTIVE_BY_DEFAULT = config.getBoolean("flint_and_steel_explode_active_by_default", true);

        // Refill Hunger
        REFILL_HUNGER_ACTIVE_BY_DEFAULT = config.getBoolean("refill_hunger_active_by_default", true);
        REFILL_HUNGER_COOLDOWN_SECONDS = config.getInt("refill_hunger_cooldown_seconds", 30);
        REFILL_HUNGER_AMOUNT = config.getInt("refill_hunger_amount", 3);

        // Tag Bomb
        TAG_BOMB_ACTIVE_BY_DEFAULT = config.getBoolean("tag_bomb_active_by_default", true);

        // Upgraded Armor
        UPGRADED_ARMOR_ACTIVE_BY_DEFAULT = config.getBoolean("upgraded_armor_active_by_default", true);

        // Bastion
        BASTION_ACTIVE_BY_DEFAULT = config.getBoolean("bastion_active_by_default", true);
        BASTION_EXPLOSION_DAMAGE_REDUCTION_IN_RANGE = config.getInt("bastion_explosion_damage_reduction_in_range", 50);
        BASTION_BASE_RANGE = config.getInt("base_bastion_range", 8);
        BASTION_MAX_RANGE = config.getInt("max_bastion_range", 20);
        BASTION_UPGRADE_AMOUNT = config.getInt("bastion_upgrade_amount", 448);

        // Keep Hotbar
        KEEP_HOTBAR_ACTIVE_BY_DEFAULT = config.getBoolean("keep_hotbar_active_by_default", true);

        // Creepers Lay Eggs
        CREEPERS_LAY_EGGS_ACTIVE_BY_DEFAULT = config.getBoolean("creepers_lay_eggs_active_by_default", true);
        CREEPERS_LAY_EGGS_MIN_SECONDS =  config.getInt("creepers_lay_eggs_min_seconds", 60);
        CREEPERS_LAY_EGGS_MAX_SECONDS =   config.getInt("creepers_lay_eggs_max_seconds", 200);

        // ===== inactive by default =====

        // Phantoms Suicide Bombers
        PHANTOMS_SUICIDE_BOMBERS_ACTIVE_BY_DEFAULT = config.getBoolean("phantoms_suicide_bombers_active_by_default", false);
        PHANTOMS_SUICIDE_BOMBERS_EXPLOSION_POWER = (float) config.getDouble("phantoms_suicide_bombers_explosion_power", 2.0);
        PHANTOMS_SUICIDE_BOMBERS_ATTEMPT_COOLDOWN_SECONDS = config.getInt("phantoms_suicide_bombers_attempt_cooldown_seconds", 60);
        PHANTOMS_SUICIDE_BOMBERS_ADDED_PERCENTAGE_ON_FAIL = config.getInt("phantoms_suicide_bombers_added_percentage_on_fail", 3);
        PHANTOMS_SUICIDE_BOMBERS_CHANCE_PERCENTAGE = config.getInt("phantoms_suicide_bombers_chance_percentage", 8);

        // Chance Drop TNT
        CHANCE_DROP_TNT_ACTIVE_BY_DEFAULT = config.getBoolean("chance_drop_tnt_active_by_default", false);
        CHANCE_DROP_TNT_CHANCE_PERCENT = config.getInt("chance_drop_tnt_chance_percent", 5);
        CHANCE_DROP_TNT_CHECK_INTERVAL_SECONDS = config.getInt("chance_drop_tnt_check_interval_seconds", 30);

        // TNT Shower
        TNT_SHOWER_ACTIVE_BY_DEFAULT = config.getBoolean("tnt_shower_active_by_default", false);
        TNT_SHOWER_CHANCE_PERCENTAGE = config.getInt("tnt_shower_chance_percentage", 1);
        TNT_SHOWER_COOLDOWN_SECONDS = config.getInt("tnt_shower_cooldown_seconds", 120);
        TNT_SHOWER_PERCENTAGE_INCREASE_ON_FAIL = config.getInt("tnt_shower_percentage_increase_on_fail", 2);
        // TNT Shower - Random Shower
        TNT_SHOWER_RANDOM_SHOWER_TOTAL_SPAWN = config.getInt("tnt_shower_random_shower_total_spawn", 12);
        TNT_SHOWER_RANDOM_SHOWER_INTERVAL_TICKS = config.getInt("tnt_shower_random_shower_interval_ticks", 20);
        TNT_SHOWER_RANDOM_SHOWER_HEIGHT_ABOVE_PLAYER = config.getInt("tnt_shower_random_shower_height_above_player", 12);
        TNT_SHOWER_RANDOM_SHOWER_RADIUS = config.getInt("tnt_shower_random_shower_radius", 10);
        TNT_SHOWER_RANDOM_SHOWER_FUSE_TICKS = config.getInt("tnt_shower_random_shower_fuse_ticks", 10);
        // TNT Shower - Pop-in Shower
        TNT_SHOWER_POPIN_SHOWER_TOTAL_SPAWNS = config.getInt("tnt_shower_popin_shower_total_spawns", 4);
        TNT_SHOWER_POPIN_SHOWER_INTERVAL_TICKS = config.getInt("tnt_shower_popin_shower_interval_ticks", 80);
        TNT_SHOWER_POPIN_SHOWER_FUSE_TICKS = config.getInt("tnt_shower_popin_shower_fuse_ticks", 40);
        TNT_SHOWER_POPIN_SHOWER_SEATED_FUSE_TICKS = config.getInt("tnt_shower_popin_shower_seated_fuse_ticks", 60);
        // TNT Shower - Sniper Shower
        TNT_SHOWER_SNIPER_SHOWER_TOTAL_SPAWNS = config.getInt("tnt_shower_sniper_shower_total_spawns", 4);
        TNT_SHOWER_SNIPER_SHOWER_INTERVAL_TICKS = config.getInt("tnt_shower_sniper_shower_interval_ticks" , 100);
        TNT_SHOWER_SNIPER_SHOWER_HEIGHT_ABOVE_PLAYER = config.getInt("tnt_shower_sniper_shower_height_above_player", 20);
        TNT_SHOWER_SNIPER_SHOWER_RADIUS = config.getInt("tnt_shower_sniper_shower_radius", 30);
        TNT_SHOWER_SNIPER_SHOWER_DELAY_BEFORE_LAUNCH_TICKS = config.getInt("tnt_shower_sniper_shower_delay_before_launch_ticks", 30);
        TNT_SHOWER_SNIPER_SHOWER_SPEED_PER_TICK = config.getInt("tnt_shower_sniper_shower_speed_per_tick", 4);
        TNT_SHOWER_SNIPER_SHOWER_EXPLOSION_MARGIN_TICKS = config.getInt("tnt_shower_sniper_shower_explosion_margin_ticks", 1);

        // Mine Field
        MINE_FIELD_ACTIVE_BY_DEFAULT = config.getBoolean("mine_field_active_by_default", false);
        MINE_FIELD_COOLDOWN_SECONDS = config.getInt("mine_field_cooldown_seconds", 120);
        MINE_FIELD_EXPLOSION_POWER = (float) config.getDouble("mine_field_explosion_power", 5.0);

        // Ores Explode
        ORES_EXPLODE_ACTIVE_BY_DEFAULT = config.getBoolean("ores_explode_active_by_default", false);
        ORES_EXPLODE_COAL_ORE = (float) config.getDouble("ores_explode_coal_ore", 2.0);
        ORES_EXPLODE_IRON_ORE = (float) config.getDouble("ores_explode_iron_ore", 3.0);
        ORES_EXPLODE_GOLD_ORE = (float) config.getDouble("ores_explode_gold_ore", 4.0);
        ORES_EXPLODE_DIAMOND_ORE = (float) config.getDouble("ores_explode_diamond_ore", 5.0);
        ORES_EXPLODE_EMERALD_ORE = (float) config.getDouble("ores_explode_emerald_ore", 5.0);
        ORES_EXPLODE_NETHER_QUARTZ_ORE = (float) config.getDouble("ores_explode_nether_quartz_ore", 5.0);
        ORES_EXPLODE_DEEPSLATE_COAL_ORE = (float) config.getDouble("ores_explode_deepslate_coal_ore", 2.5);
        ORES_EXPLODE_DEEPSLATE_IRON_ORE =  (float) config.getDouble("ores_explode_iron_ore", 2.5);
        ORES_EXPLODE_DEEPSLATE_GOLD_ORE = (float) config.getDouble("ores_explode_gold_ore", 2.5);
        ORES_EXPLODE_DEEPSLATE_DIAMOND_ORE = (float) config.getDouble("ores_explode_diamond_ore", 2.5);
        ORES_EXPLODE_DEEPSLATE_EMERALD_ORE =  (float) config.getDouble("ores_explode_emerald_ore", 2.5);

        // Enderman Wave
        ENDERMAN_WAVE_ACTIVE_BY_DEFAULT = config.getBoolean("enderman_wave_active_by_default", false);
        ENDERMAN_WAVE_ATTEMPT_CHANCE = config.getInt("enderman_wave_attempt_chance", 1);
        ENDERMAN_WAVE_COOLDOWN_SECONDS = config.getInt("enderman_wave_cooldown_seconds", 120);
    }

    // Public static getters for each default option

    // General Configs
    public static int get_wave_duration_seconds() {
        Boomba.getInstance().getLogger().info("Wave duration seconds: " + WAVE_DURATION_SECONDS);
        return WAVE_DURATION_SECONDS;
    }
    public static int get_max_difficulty() {
        Boomba.getInstance().getLogger().info("Max difficulty: " + MAX_DIFFICULTY);
        return MAX_DIFFICULTY;
    }


    // All Explode on Death
    public static boolean isAllExplodeOnDeathActiveByDefault() {
        return ALL_EXPLODE_ON_DEATH_ACTIVE_BY_DEFAULT;
    }
    public static float get_all_explode_on_death_hearts_per_explosion_power() {
        Boomba.getInstance().getLogger().info("All Explode on Death hearts per explosion power: " + ALL_EXPLODE_ON_DEATH_HEARTS_PER_EXPLOSION_POWER);
        return ALL_EXPLODE_ON_DEATH_HEARTS_PER_EXPLOSION_POWER;
    }
    public static int get_all_explode_on_death_difficulty_per_explosion_power() {
        Boomba.getInstance().getLogger().info("All Explode on Death difficulty per explosion power: " + ALL_EXPLODE_ON_DEATH_DIFFICULTY_PER_EXPLOSION_POWER);
        return ALL_EXPLODE_ON_DEATH_DIFFICULTY_PER_EXPLOSION_POWER;
    }

    // Chance Drop Tnt
    public static boolean isChanceDropTntActiveByDefault() {
        return CHANCE_DROP_TNT_ACTIVE_BY_DEFAULT;
    }
    public static int get_chance_drop_tnt_check_interval_seconds() {
        Boomba.getInstance().getLogger().info("Chance Drop TNT check interval seconds: " + CHANCE_DROP_TNT_CHECK_INTERVAL_SECONDS);
        return CHANCE_DROP_TNT_CHECK_INTERVAL_SECONDS;
    }
    public static int get_chance_drop_tnt_chance_percent() {
        Boomba.getInstance().getLogger().info("Chance Drop TNT chance percentage: " + CHANCE_DROP_TNT_ACTIVE_BY_DEFAULT);
        return CHANCE_DROP_TNT_CHANCE_PERCENT;
    }

    // Creepers High knockback
    public static boolean isCreepersHighKnockbackActiveByDefault() {
        return CREEPERS_HIGH_KNOCKBACK_ACTIVE_BY_DEFAULT;
    }
    public static double get_creepers_high_knockback_knockback_multiplier() {
        Boomba.getInstance().getLogger().info("Creepers High Knockback knockback multiplier: " + CREEPERS_HIGH_KNOCKBACK_KNOCKBACK_MULTIPLIER);
        return CREEPERS_HIGH_KNOCKBACK_KNOCKBACK_MULTIPLIER;
    }

    // Enderman Explosive Teleport
    public static boolean isEndermanExplosiveTeleportActiveByDefault() {return ENDERMAN_EXPLOSIVE_TELEPORT_ACTIVE_BY_DEFAULT;}
    public static float get_enderman_explosive_teleport_explosion_power() {
        Boomba.getInstance().getLogger().info("Enderman Explosive Teleport explosion power: " + ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER);
        return ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER;
    }
    public static float get_enderman_explosive_teleport_explosion_power_per_difficulty() {
        Boomba.getInstance().getLogger().info("Enderman Explosive Teleport explosion power per difficulty: " + ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER_PER_DIFFICULTY);
        return ENDERMAN_EXPLOSIVE_TELEPORT_EXPLOSION_POWER_PER_DIFFICULTY;
    }

    // Phantoms Suicide Bombers
    public static boolean isPhantomsSuicideBombersActiveByDefault() {return PHANTOMS_SUICIDE_BOMBERS_ACTIVE_BY_DEFAULT;}
    public static float get_phantom_suicide_bombers_explosion_power() {
        Boomba.getInstance().getLogger().info("Phantom Suicide Bombers explosion power: " + PHANTOMS_SUICIDE_BOMBERS_EXPLOSION_POWER);
        return PHANTOMS_SUICIDE_BOMBERS_EXPLOSION_POWER;
    }
    public static int get_phantom_suicide_bombers_attempt_cooldown_seconds() {
        Boomba.getInstance().getLogger().info("Phantom Suicide Bombers attempt cooldown seconds: " + PHANTOMS_SUICIDE_BOMBERS_ATTEMPT_COOLDOWN_SECONDS);
        return PHANTOMS_SUICIDE_BOMBERS_ATTEMPT_COOLDOWN_SECONDS;
    }
    public static int get_phantom_suicide_bombers_added_percentage_on_fail() {
        Boomba.getInstance().getLogger().info("Phantom Suicide Bombers added percentage on fail: " + PHANTOMS_SUICIDE_BOMBERS_ADDED_PERCENTAGE_ON_FAIL);
        return PHANTOMS_SUICIDE_BOMBERS_ADDED_PERCENTAGE_ON_FAIL;
    }
    public static int get_phantom_suicide_bombers_chance_percentage() {
        Boomba.getInstance().getLogger().info("Phantom Suicide Bombers chance percentage: " + PHANTOMS_SUICIDE_BOMBERS_CHANCE_PERCENTAGE);
        return PHANTOMS_SUICIDE_BOMBERS_CHANCE_PERCENTAGE;
    }

    // Explosive Punch
    public static boolean isExplosivePunchActiveByDefault() {
        return EXPLOSIVE_PUNCH_ACTIVE_BY_DEFAULT;
    }
    public static double get_explosive_punch_minimum_damage_threshold() {
        Boomba.getInstance().getLogger().info("Explosive Punch minimum damage threshold:" + EXPLOSIVE_PUNCH_MINIMUM_DAMAGE_THRESHOLD);
        return EXPLOSIVE_PUNCH_MINIMUM_DAMAGE_THRESHOLD;
    }

    // Fall Damage Explode
    public static boolean isFallDamageExplodeActiveByDefault() {
        return FALL_DAMAGE_EXPLODE_ACTIVE_BY_DEFAULT;
    }
    public static int get_fall_damage_explode_cooldown(){
        Boomba.getInstance().getLogger().info("Fall Damage Explode cooldown: " + FALL_DAMAGE_EXPLODE_COOLDOWN);
        return FALL_DAMAGE_EXPLODE_COOLDOWN;
    }
    public static int get_fall_damage_explode_power_per_fall_distance() {
        Boomba.getInstance().getLogger().info("Fall Damage Explode power per fall distance: " + FALL_DAMAGE_EXPLODE_POWER_PER_FALL_DISTANCE);
        return FALL_DAMAGE_EXPLODE_POWER_PER_FALL_DISTANCE;
    }
    public static int get_fall_damage_explode_explosion_count_per_explosion_power() {
        Boomba.getInstance().getLogger().info("Fall Damage Explode explosion count per explosion power: " + FALL_DAMAGE_EXPLODE_EXPLOSION_COUNT_PER_EXPLOSION_POWER);
        return FALL_DAMAGE_EXPLODE_EXPLOSION_COUNT_PER_EXPLOSION_POWER;
    }

    // Flint And Steel Explode
    public static boolean isFlintAndSteelExplodeActiveByDefault() {
        return FLINT_AND_STEEL_EXPLODE_ACTIVE_BY_DEFAULT;
    }

    // Refill Hunger
    public static boolean isRefillHungerActiveByDefault() {
        return REFILL_HUNGER_ACTIVE_BY_DEFAULT;
    }
    public static int get_refill_hunger_cooldown_seconds() {
        Boomba.getInstance().getLogger().info("Refill Hunger cooldown seconds: " + REFILL_HUNGER_COOLDOWN_SECONDS);
        return REFILL_HUNGER_COOLDOWN_SECONDS;
    }
    public static int get_refill_hunger_amount() {
        Boomba.getInstance().getLogger().info("Refill Hunger amount: " + REFILL_HUNGER_AMOUNT);
        return REFILL_HUNGER_AMOUNT;
    }

    // Tag Bomb
    public static boolean isTagBombActiveByDefault() {
        return TAG_BOMB_ACTIVE_BY_DEFAULT;
    }

    // TNT Shower
    public static boolean isTntShowerActiveByDefault() {
        return TNT_SHOWER_ACTIVE_BY_DEFAULT;
    }
    // TNT Shower - Random Shower
    public static int get_tnt_shower_random_shower_total_spawn() {
        Boomba.getInstance().getLogger().info("TNT Shower random shower total spawn: " + TNT_SHOWER_RANDOM_SHOWER_TOTAL_SPAWN);
        return TNT_SHOWER_RANDOM_SHOWER_TOTAL_SPAWN;
    }
    public static long get_tnt_shower_random_shower_interval_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower random shower interval ticks: " + TNT_SHOWER_RANDOM_SHOWER_INTERVAL_TICKS);
        return TNT_SHOWER_RANDOM_SHOWER_INTERVAL_TICKS;
    }
    public static int get_tnt_shower_random_shower_height_above_player() {
        Boomba.getInstance().getLogger().info("TNT Shower random shower height above player: " + TNT_SHOWER_RANDOM_SHOWER_HEIGHT_ABOVE_PLAYER);
        return TNT_SHOWER_RANDOM_SHOWER_HEIGHT_ABOVE_PLAYER;
    }
    public static int get_tnt_shower_random_shower_raduis() {
        Boomba.getInstance().getLogger().info("TNT Shower random shower radius: " + TNT_SHOWER_RANDOM_SHOWER_RADIUS);
        return TNT_SHOWER_RANDOM_SHOWER_RADIUS;
    }
    public static int get_tnt_shower_random_shower_fuse_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower random shower fuse ticks: " + TNT_SHOWER_RANDOM_SHOWER_FUSE_TICKS);
        return TNT_SHOWER_RANDOM_SHOWER_FUSE_TICKS;
    }
    // TNT Shower - Popin Shower
    public static int get_tnt_shower_popin_shower_total_spawns() {
        Boomba.getInstance().getLogger().info("TNT Shower pop-in shower total spawns: " + TNT_SHOWER_POPIN_SHOWER_TOTAL_SPAWNS);
        return TNT_SHOWER_POPIN_SHOWER_TOTAL_SPAWNS;
    }
    public static long get_tnt_shower_popin_shower_interval_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower pop-in shower interval ticks: " + TNT_SHOWER_POPIN_SHOWER_INTERVAL_TICKS);
        return TNT_SHOWER_POPIN_SHOWER_INTERVAL_TICKS;
    }
    public static int get_tnt_shower_popin_shower_fuse_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower pop-in shower fuse ticks: " + TNT_SHOWER_POPIN_SHOWER_FUSE_TICKS);
        return TNT_SHOWER_POPIN_SHOWER_FUSE_TICKS;
    }
    public static int get_tnt_shower_popin_seated_fuse_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower pop-in shower seated fuse ticks: " + TNT_SHOWER_POPIN_SHOWER_SEATED_FUSE_TICKS);
        return TNT_SHOWER_POPIN_SHOWER_SEATED_FUSE_TICKS;
    }
    // TNT Shower - Sniper Shower
    public static int get_tnt_shower_sniper_shower_total_spawns() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower total spawns: " + TNT_SHOWER_SNIPER_SHOWER_TOTAL_SPAWNS);
        return TNT_SHOWER_SNIPER_SHOWER_TOTAL_SPAWNS;
    }
    public static long get_tnt_shower_sniper_shower_interval_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower interval ticks: " + TNT_SHOWER_SNIPER_SHOWER_INTERVAL_TICKS);
        return TNT_SHOWER_SNIPER_SHOWER_INTERVAL_TICKS;
    }
    public static int get_tnt_shower_sniper_shower_height_above_player() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower height above player: " + TNT_SHOWER_SNIPER_SHOWER_HEIGHT_ABOVE_PLAYER);
        return TNT_SHOWER_SNIPER_SHOWER_HEIGHT_ABOVE_PLAYER;
    }
    public static int get_tnt_shower_sniper_shower_radius() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower radius: " + TNT_SHOWER_SNIPER_SHOWER_RADIUS);
        return TNT_SHOWER_SNIPER_SHOWER_RADIUS;
    }
    public static long get_tnt_shower_sniper_shower_delay_before_launch_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower delay before launch ticks: " + TNT_SHOWER_SNIPER_SHOWER_DELAY_BEFORE_LAUNCH_TICKS);
        return TNT_SHOWER_SNIPER_SHOWER_DELAY_BEFORE_LAUNCH_TICKS;
    }
    public static double get_tnt_shower_sniper_shower_speed_per_tick() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower speed per tick: " + TNT_SHOWER_SNIPER_SHOWER_SPEED_PER_TICK);
        return TNT_SHOWER_SNIPER_SHOWER_SPEED_PER_TICK;
    }
    public static int get_tnt_shower_sniper_shower_explosion_margin_ticks() {
        Boomba.getInstance().getLogger().info("TNT Shower sniper shower explosion margin ticks: " + TNT_SHOWER_SNIPER_SHOWER_EXPLOSION_MARGIN_TICKS);
        return TNT_SHOWER_SNIPER_SHOWER_EXPLOSION_MARGIN_TICKS;
    }

    // Bastion
    public static boolean isBastionActiveByDefault() {
        return BASTION_ACTIVE_BY_DEFAULT;
    }
    public static int get_bastion_explosion_damage_reduction_in_range() {
        Boomba.getInstance().getLogger().info("Bastion explosion damage reduction in range: " + BASTION_EXPLOSION_DAMAGE_REDUCTION_IN_RANGE);
        return BASTION_EXPLOSION_DAMAGE_REDUCTION_IN_RANGE;
    }
    public static int get_bastion_base_range() {
        Boomba.getInstance().getLogger().info("Base bastion range: " + BASTION_BASE_RANGE);
        return BASTION_BASE_RANGE;
    }
    public static int get_bastion_max_range() {
        Boomba.getInstance().getLogger().info("Bastion max range: " + BASTION_MAX_RANGE);
        return BASTION_MAX_RANGE;
    }
    public static int get_bastion_upgrade_amount() {
        Boomba.getInstance().getLogger().info("Bastion upgrade amount: " + BASTION_UPGRADE_AMOUNT);
        return BASTION_UPGRADE_AMOUNT;
    }

    // Keep Hotbar
    public static boolean isKeepHotBarActiveByDefault() {
        return KEEP_HOTBAR_ACTIVE_BY_DEFAULT;
    }

    // Creepers Lay Eggs
    public static boolean isCreepersLayEggsActiveByDefault() {
        return CREEPERS_LAY_EGGS_ACTIVE_BY_DEFAULT;
    }
    public static int get_creepers_lay_eggs_min_seconds() {
        Boomba.getInstance().getLogger().info("Creepers Lay Eggs min seconds:" + CREEPERS_LAY_EGGS_MIN_SECONDS);
        return CREEPERS_LAY_EGGS_MIN_SECONDS;
    }
    public static int get_creepers_lay_eggs_max_seconds() {
        Boomba.getInstance().getLogger().info("CREEPERS_LAY_EGGS_MAX_SECONDS:" + CREEPERS_LAY_EGGS_MAX_SECONDS);
        return CREEPERS_LAY_EGGS_MAX_SECONDS;
    }

    // Mine Field
    public static boolean isMineFieldActiveByDefault() {
        return MINE_FIELD_ACTIVE_BY_DEFAULT;
    }
    public static int get_mine_field_cooldown_seconds() {
        Boomba.getInstance().getLogger().info("Mine Field cooldown seconds: " + MINE_FIELD_COOLDOWN_SECONDS);
        return MINE_FIELD_COOLDOWN_SECONDS;
    }
    public static float get_mine_field_explosion_power() {
        Boomba.getInstance().getLogger().info("Mine Field explosion power: " + MINE_FIELD_EXPLOSION_POWER);
        return MINE_FIELD_EXPLOSION_POWER;
    }

    // Ores Explode
    public static boolean isOresExplodeActiveByDefault() {
        return ORES_EXPLODE_ACTIVE_BY_DEFAULT;
    }
    public static float get_ores_explode_coal_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode coal ore explosion power: " + ORES_EXPLODE_COAL_ORE);
        return ORES_EXPLODE_COAL_ORE;
    }
    public static float get_ores_explode_iron_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode iron ore explosion power: " + ORES_EXPLODE_IRON_ORE);
        return ORES_EXPLODE_IRON_ORE;
    }
    public static float get_ores_explode_gold_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode gold ore explosion power: " + ORES_EXPLODE_GOLD_ORE);
        return ORES_EXPLODE_GOLD_ORE;
    }
    public static float get_ores_explode_diamond_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode diamond ore explosion power: " + ORES_EXPLODE_DIAMOND_ORE);
        return ORES_EXPLODE_DIAMOND_ORE;
    }
    public static float get_ores_explode_emerald_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode emerald ore explosion power: " + ORES_EXPLODE_EMERALD_ORE);
        return ORES_EXPLODE_EMERALD_ORE;
    }
    public static float get_ores_explode_nether_quartz_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode nether quartz ore explosion power: " + ORES_EXPLODE_NETHER_QUARTZ_ORE);
        return ORES_EXPLODE_NETHER_QUARTZ_ORE;
    }
    public static float get_ores_explode_deepslate_coal_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode deepslate coal ore explosion power: " + ORES_EXPLODE_DEEPSLATE_COAL_ORE);
        return ORES_EXPLODE_DEEPSLATE_COAL_ORE;
    }
    public static float get_ores_explode_deepslate_iron_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode deepslate iron ore explosion power: " + ORES_EXPLODE_DEEPSLATE_IRON_ORE);
        return ORES_EXPLODE_DEEPSLATE_IRON_ORE;
    }
    public static float get_ores_explode_deepslate_gold_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode deepslate gold ore explosion power: " + ORES_EXPLODE_DEEPSLATE_GOLD_ORE);
        return ORES_EXPLODE_DEEPSLATE_GOLD_ORE;
    }
    public static float get_ores_explode_deepslate_diamond_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode deepslate diamond ore explosion power: " + ORES_EXPLODE_DEEPSLATE_DIAMOND_ORE);
        return ORES_EXPLODE_DEEPSLATE_DIAMOND_ORE;
    }
    public static float get_ores_explode_deepslate_emerald_ore() {
        Boomba.getInstance().getLogger().info("Ores Explode deepslate emerald ore explosion power: " + ORES_EXPLODE_DEEPSLATE_EMERALD_ORE);
        return ORES_EXPLODE_DEEPSLATE_EMERALD_ORE;
    }

    public static boolean isEndermenWaveActiveByDefault() {
        return ENDERMAN_WAVE_ACTIVE_BY_DEFAULT;
    }
    public static int get_enderman_wave_attempt_chance() {
        Boomba.getInstance().getLogger().info("Enderman Wave attempt chance: " + ENDERMAN_WAVE_ATTEMPT_CHANCE);
        return ENDERMAN_WAVE_ATTEMPT_CHANCE;
    }
    public static int get_enderman_wave_cooldown_seconds() {
        Boomba.getInstance().getLogger().info("Enderman Wave cooldown seconds: " + ENDERMAN_WAVE_COOLDOWN_SECONDS);
        return ENDERMAN_WAVE_COOLDOWN_SECONDS;
    }

    public static boolean isUpgradedArmorActiveByDefault() {
        return UPGRADED_ARMOR_ACTIVE_BY_DEFAULT;
    }

    public static boolean isExplosiveSackActiveByDefault() {
        return EXPLOSIVE_PUNCH_ACTIVE_BY_DEFAULT;
    }

    public static int get_tnt_shower_chance_percentage() {
        return TNT_SHOWER_CHANCE_PERCENTAGE;
    }

    public static long get_tnt_shower_cooldown_seconds() {
        return TNT_SHOWER_COOLDOWN_SECONDS;
    }

    public static int get_tnt_shower_percentage_increase_on_fail() {
        return TNT_SHOWER_PERCENTAGE_INCREASE_ON_FAIL;
    }
}
