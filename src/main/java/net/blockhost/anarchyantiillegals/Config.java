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
    }
}
