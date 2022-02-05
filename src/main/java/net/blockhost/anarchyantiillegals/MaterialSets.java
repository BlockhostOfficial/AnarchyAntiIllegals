package net.blockhost.anarchyantiillegals;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MaterialSets {
    public static final Set<Material> armorMaterials = EnumSet.noneOf(Material.class);

    public static final Set<Material> weaponMaterials = EnumSet.noneOf(Material.class);

    public static Set<Material> illegalBlocks = new HashSet<>();

    public static Map<Material, Integer> limitedInShulkers = new EnumMap<>(Material.class);

    public static void load(FileConfiguration config, AntiIllegals plugin) {
        illegalBlocks.clear();
        config.getStringList("illegalBlocks").forEach(material -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
            if (xMaterial.isPresent()) {
                illegalBlocks.add(xMaterial.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Invalid illegal material: " + material);
            }
        });

        limitedInShulkers.clear();
        config.getConfigurationSection("limitedInShulkers").getKeys(false).forEach(key -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(key);
            if (xMaterial.isPresent()) {
                limitedInShulkers.put(xMaterial.get().parseMaterial(), config.getInt("limitedInShulkers." + key));
            } else {
                plugin.getLogger().warning("Invalid material in limitedInShulkers: " + key);
            }
        });

        armorMaterials.clear();
        config.getStringList("armorMaterials").forEach(material -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
            if (xMaterial.isPresent()) {
                armorMaterials.add(xMaterial.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Invalid armor material: " + material);
            }
        });

        weaponMaterials.clear();
        config.getStringList("weaponMaterials").forEach(material -> {
            Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
            if (xMaterial.isPresent()) {
                weaponMaterials.add(xMaterial.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Invalid weapon material: " + material);
            }
        });
    }
}
