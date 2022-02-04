package net.blockhost.anarchyantiillegals;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MaterialSets {
    public static final Set<Material> armorMaterials = EnumSet.of(
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,

            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,

            Material.GOLD_HELMET,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS,

            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
    );

    public static final Set<Material> weaponMaterials = EnumSet.of(
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLD_AXE,
            Material.DIAMOND_AXE,

            Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLD_SWORD,
            Material.DIAMOND_SWORD,

            Material.BOW
    );

    public static Set<Material> illegalBlocks = new HashSet<>();

    public static Map<Material, Integer> limitedInShulkers = new EnumMap<>(Material.class);

    public static void load(FileConfiguration config) {
        illegalBlocks.clear();
        config.getStringList("illegalBlocks").forEach(material -> {
            Material materialObject = Material.getMaterial(material);
            if (materialObject != null) {
                illegalBlocks.add(materialObject);
            }
        });

        limitedInShulkers.clear();
        config.getConfigurationSection("limitedInShulkers").getKeys(false).forEach(key -> {
            Material materialObject = Material.getMaterial(key);
            if (materialObject != null) {
                limitedInShulkers.put(materialObject, config.getInt("limitedInShulkers." + key));
            }
        });
    }
}
