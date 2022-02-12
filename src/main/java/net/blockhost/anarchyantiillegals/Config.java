package net.blockhost.anarchyantiillegals;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static boolean REMOVE_ILLEGALS;
    public static boolean REMOVE_BOOKS;
    public static boolean REMOVE_COLOR;
    public static boolean REMOVE_LORE;
    public static boolean REMOVE_OVER_STACKED;
    public static boolean REVERT_ENCHANTMENTS_MAX;
    public static boolean REVERT_ENCHANTMENTS_CONFLICTING;
    public static boolean CHECK_SHULKERS;
    public static boolean FIX_UNBREAKABLE;
    public static boolean REVERT_FURNACE;
    public static boolean MAX_BOOKS_IN_SHULKER;
    public static int MAX_BOOKS;
    public static boolean DROP_BOOKS;
    public static boolean LIMIT_IN_SHULKERS;
    public static boolean FORCE_ASCII_DISPLAY_NAME;
    public static boolean EVENT_BLOCK_BREAK;
    public static boolean EVENT_BLOCK_PLACE;
    public static boolean EVENT_VEHICLE_DESTROY;
    public static boolean EVENT_PLAYER_ITEM_DROP;
    public static boolean EVENT_ENTITY_PICKUP_ITEM;
    public static boolean EVENT_ENTITY_DEATH;
    public static boolean EVENT_PLAYER_SWAP_HAND_ITEMS;
    public static boolean EVENT_PLAYER_ITEM_HELD;
    public static boolean EVENT_INVENTORY_MOVE_ITEM;
    public static boolean EVENT_PLAYER_INTERACT_ENTITY;
    public static boolean EVENT_HANGING_BREAK;
    public static boolean EVENT_ENTITY_DAMAGE_BY_ENTITY;
    public static boolean EVENT_INVENTORY_CLICK;
    public static boolean EVENT_INVENTORY_OPEN;
    public static boolean EVENT_BLOCK_DISPENSE;

    public static void load(FileConfiguration config) {
        REMOVE_ILLEGALS = config.getBoolean("removeIllegals");
        REMOVE_BOOKS = config.getBoolean("removeBooks");
        REMOVE_COLOR = config.getBoolean("nameColorCheck");
        REMOVE_LORE = config.getBoolean("removeLore");
        REMOVE_OVER_STACKED = config.getBoolean("removeOverstacked");
        REVERT_ENCHANTMENTS_MAX = config.getBoolean("revertEnchantmentsMax");
        REVERT_ENCHANTMENTS_CONFLICTING = config.getBoolean("revertEnchantmentsConflicting");
        CHECK_SHULKERS = config.getBoolean("checkShulkers");
        FIX_UNBREAKABLE = config.getBoolean("fixUnbreakable");
        REVERT_FURNACE = config.getBoolean("revertFurnace");
        MAX_BOOKS_IN_SHULKER = config.getBoolean("maxBooksInShulker");
        MAX_BOOKS = config.getInt("maxBooks");
        DROP_BOOKS = config.getBoolean("dropBooks");
        LIMIT_IN_SHULKERS = config.getBoolean("limitInShulkers");
        FORCE_ASCII_DISPLAY_NAME = config.getBoolean("forceAsciiDisplayName");

        EVENT_BLOCK_BREAK = config.getBoolean("eventBlockBreak");
        EVENT_BLOCK_PLACE = config.getBoolean("eventBlockPlace");
        EVENT_VEHICLE_DESTROY = config.getBoolean("eventVehicleDestroy");
        EVENT_PLAYER_ITEM_DROP = config.getBoolean("eventPlayerItemDrop");
        EVENT_ENTITY_PICKUP_ITEM = config.getBoolean("eventEntityPickupItem");
        EVENT_ENTITY_DEATH = config.getBoolean("eventEntityDeath");
        EVENT_PLAYER_SWAP_HAND_ITEMS = config.getBoolean("eventPlayerSwapHandItems");
        EVENT_PLAYER_ITEM_HELD = config.getBoolean("eventPlayerItemHeld");
        EVENT_INVENTORY_MOVE_ITEM = config.getBoolean("eventInventoryMoveItem");
        EVENT_PLAYER_INTERACT_ENTITY = config.getBoolean("eventPlayerInteractEntity");
        EVENT_HANGING_BREAK = config.getBoolean("eventHangingBreak");
        EVENT_ENTITY_DAMAGE_BY_ENTITY = config.getBoolean("eventEntityDamageByEntity");
        EVENT_INVENTORY_CLICK = config.getBoolean("eventInventoryClick");
        EVENT_INVENTORY_OPEN = config.getBoolean("eventInventoryOpen");
        EVENT_BLOCK_DISPENSE = config.getBoolean("eventBlockDispense");
    }
}
