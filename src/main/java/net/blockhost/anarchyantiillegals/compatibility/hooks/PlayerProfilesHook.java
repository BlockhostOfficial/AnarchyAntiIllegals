package net.blockhost.anarchyantiillegals.compatibility.hooks;

import me.linoxgh.playerprofiles.PlayerProfiles;
import net.blockhost.anarchyantiillegals.compatibility.PluginHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class PlayerProfilesHook extends PluginHook {

    public PlayerProfilesHook(String pluginName) {
        super(pluginName);
    }

    @Override public boolean checkInventory(HumanEntity player, Inventory inventory) {
        if (!Bukkit.getPluginManager().isPluginEnabled(getPluginName())) return true;
        return PlayerProfiles.getPlugin(PlayerProfiles.class).getGm().getOpenGUI(inventory) == null;
    }
}
