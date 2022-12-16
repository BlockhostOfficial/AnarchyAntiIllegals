package net.blockhost.anarchyantiillegals.compatibility;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public abstract class PluginHook {

    private final String pluginName;

    protected PluginHook(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Should the plugin check for illegal items in this inventory.
     *
     * @param player    The viewer of the inventory.
     * @param inventory The inventory.
     * @return  Whether the plugin should check this inventory.
     */
    public abstract boolean checkInventory(HumanEntity player, Inventory inventory);

    public String getPluginName() {
        return pluginName;
    }
}
