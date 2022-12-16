package net.blockhost.anarchyantiillegals.compatibility;

import net.blockhost.anarchyantiillegals.AnarchyAntiIllegals;
import net.blockhost.anarchyantiillegals.compatibility.hooks.DeluxeMenusHook;
import net.blockhost.anarchyantiillegals.compatibility.hooks.PlayerProfilesHook;

import java.util.HashSet;
import java.util.Set;

public class CompatibilityManager {

    private static final Set<PluginHook> hooks = new HashSet<>();

    public static void load(AnarchyAntiIllegals plugin) {
        hooks.add(new DeluxeMenusHook("DeluxeMenus"));
        hooks.add(new PlayerProfilesHook("PlayerProfiles"));
    }

    public static Set<PluginHook> getHooks() {
        return hooks;
    }
}
