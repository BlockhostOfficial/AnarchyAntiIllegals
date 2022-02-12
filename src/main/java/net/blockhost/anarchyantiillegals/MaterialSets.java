package net.blockhost.anarchyantiillegals;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MaterialSets {
    protected static final Set<Material> ARMOR_MATERIALS = EnumSet.noneOf(Material.class);

    protected static final Set<Material> WEAPON_MATERIALS = EnumSet.noneOf(Material.class);

    protected static final Set<Material> ILLEGAL_BLOCKS = new HashSet<>();

    protected static final Map<Material, Integer> LIMITED_IN_SHULKERS = new EnumMap<>(Material.class);

    private MaterialSets() {
    }

    public static void load(FileConfiguration config, AntiIllegals plugin) {
        ILLEGAL_BLOCKS.clear();
        config.getStringList("illegalBlocks").forEach(material -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
            if (xMaterial.isPresent()) {
                ILLEGAL_BLOCKS.add(xMaterial.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Invalid illegal material: " + material);
            }
        });

        LIMITED_IN_SHULKERS.clear();
        config.getConfigurationSection("limitedInShulkers").getKeys(false).forEach(key -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(key);
            if (xMaterial.isPresent()) {
                LIMITED_IN_SHULKERS.put(xMaterial.get().parseMaterial(), config.getInt("limitedInShulkers." + key));
            } else {
                plugin.getLogger().warning("Invalid material in limitedInShulkers: " + key);
            }
        });

        ARMOR_MATERIALS.clear();
        config.getStringList("armorMaterials").forEach(material -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
            if (xMaterial.isPresent()) {
                ARMOR_MATERIALS.add(xMaterial.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Invalid armor material: " + material);
            }
        });

        WEAPON_MATERIALS.clear();
        config.getStringList("weaponMaterials").forEach(material -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
            if (xMaterial.isPresent()) {
                WEAPON_MATERIALS.add(xMaterial.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Invalid weapon material: " + material);
            }
        });
    }
}
