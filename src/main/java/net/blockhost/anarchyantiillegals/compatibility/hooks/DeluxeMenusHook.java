package net.blockhost.anarchyantiillegals.compatibility.hooks;

import com.extendedclip.deluxemenus.menu.Menu;
import net.blockhost.anarchyantiillegals.compatibility.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DeluxeMenusHook extends PluginHook {

    public DeluxeMenusHook(String pluginName) {
        super(pluginName);
    }

    @Override public boolean checkInventory(HumanEntity player, Inventory inventory) {
        if (!Bukkit.getPluginManager().isPluginEnabled(getPluginName())) return true;
        return !Menu.inMenu((Player) player);
    }
}
