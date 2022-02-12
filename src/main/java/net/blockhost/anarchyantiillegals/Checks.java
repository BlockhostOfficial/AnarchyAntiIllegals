package net.blockhost.anarchyantiillegals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static net.blockhost.anarchyantiillegals.MaterialSets.*;

public class Checks {
    private Checks() {
    }

    public static boolean isIllegalBlock(final Material material) {
        if (material == null) {
            return false;
        }

        return ILLEGAL_BLOCKS.contains(material);
    }

    public static boolean isArmor(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return ARMOR_MATERIALS.contains(itemStack.getType());
    }

    public static boolean isWeapon(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return WEAPON_MATERIALS.contains(itemStack.getType());
    }
}
