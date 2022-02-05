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

    public static void load(FileConfiguration config) {
        illegalBlocks.clear();
        config.getStringList("illegalBlocks").forEach(material ->
                XMaterial.matchXMaterial(material).ifPresent(
                        material1 -> illegalBlocks.add(material1.parseMaterial()))
        );

        limitedInShulkers.clear();
        config.getConfigurationSection("limitedInShulkers").getKeys(false).forEach(key ->
                XMaterial.matchXMaterial(key).ifPresent(
                        material -> limitedInShulkers.put(
                                material.parseMaterial(),
                                config.getInt("limitedInShulkers." + key)))
        );

        armorMaterials.clear();
        config.getStringList("armorMaterials").forEach(material ->
                XMaterial.matchXMaterial(material).ifPresent(
                        material1 -> armorMaterials.add(material1.parseMaterial())
                )
        );

        weaponMaterials.clear();
        config.getStringList("weaponMaterials").forEach(material ->
                XMaterial.matchXMaterial(material).ifPresent(
                        material1 -> weaponMaterials.add(material1.parseMaterial())
                )
        );
    }
}
