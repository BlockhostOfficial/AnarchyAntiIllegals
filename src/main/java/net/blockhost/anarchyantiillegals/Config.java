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
    public static boolean eventBlockBreak;
    public static boolean eventBlockPlace;
    public static boolean eventVehicleDestroy;
    public static boolean eventPlayerItemDrop;
    public static boolean eventEntityPickupItem;
    public static boolean eventEntityDeath;
    public static boolean eventPlayerSwapHandItems;
    public static boolean eventPlayerItemHeld;
    public static boolean eventInventoryMoveItem;
    public static boolean eventPlayerInteractEntity;
    public static boolean eventHangingBreak;
    public static boolean eventEntityDamageByEntity;
    public static boolean eventInventoryClick;
    public static boolean eventInventoryOpen;
    public static boolean eventBlockDispense;

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
        eventBlockBreak = config.getBoolean("eventBlockBreak");
        eventBlockPlace = config.getBoolean("eventBlockPlace");
        eventVehicleDestroy = config.getBoolean("eventVehicleDestroy");
        eventPlayerItemDrop = config.getBoolean("eventPlayerItemDrop");
        eventEntityPickupItem = config.getBoolean("eventEntityPickupItem");
        eventEntityDeath = config.getBoolean("eventEntityDeath");
        eventPlayerSwapHandItems = config.getBoolean("eventPlayerSwapHandItems");
        eventPlayerItemHeld = config.getBoolean("eventPlayerItemHeld");
        eventInventoryMoveItem = config.getBoolean("eventInventoryMoveItem");
        eventPlayerInteractEntity = config.getBoolean("eventPlayerInteractEntity");
        eventHangingBreak = config.getBoolean("eventHangingBreak");
        eventEntityDamageByEntity = config.getBoolean("eventEntityDamageByEntity");
        eventInventoryClick = config.getBoolean("eventInventoryClick");
        eventInventoryOpen = config.getBoolean("eventInventoryOpen");
        eventBlockDispense = config.getBoolean("eventBlockDispense");
    }
}
