package net.blockhost.anarchyantiillegals;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AntiIllegals extends JavaPlugin {
    private static final int maxLoreEnchantmentLevel = 1;

    public void checkInventory(Inventory inventory, Location location, boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    /**
     * use this method to check and remove illegal items from inventories
     *
     * @param inventory      the inventory that should be checked
     * @param location       location of the inventory holder for possible item drops
     * @param checkRecursive true, if items inside containers should be checked
     */
    public void checkInventory(Inventory inventory, Location location, boolean checkRecursive, boolean isInsideShulker) {
        List<ItemStack> removeItemStacks = new ArrayList<>();
        List<ItemStack> bookItemStacks = new ArrayList<>();
        Map<Material, List<ItemStack>> limitBlocksInShulker = new EnumMap<>(Material.class);

        boolean wasFixed = false;
        int fixedIllegals = 0;
        int fixedBooks = 0;
        int limited = 0;

        // Loop through Inventory
        for (ItemStack itemStack : inventory.getContents()) {
            switch (checkItemStack(itemStack, location, checkRecursive)) {
                case ILLEGAL:
                    removeItemStacks.add(itemStack);
                    break;

                case WAS_FIXED:
                    wasFixed = true;
                    break;

                // Book inside a shulker
                case WRITTEN_BOOK:
                    if (isInsideShulker || inventory.getHolder() instanceof ShulkerBox) {
                        bookItemStacks.add(itemStack);
                    }
                    break;

                case LIMITED_IN_SHULKERS:
                    if (isInsideShulker || inventory.getHolder() instanceof ShulkerBox) {
                        limitBlocksInShulker.putIfAbsent(itemStack.getType(), new ArrayList<>());

                        limitBlocksInShulker.get(itemStack.getType()).add(itemStack);
                    }
                    break;

                default:
                    break;
            }
        }

        if (Config.REMOVE_ILLEGALS) {
            // Remove illegal items - TODO: check if that is needed if setAmount(0) is in place
            for (ItemStack itemStack : removeItemStacks) {
                itemStack.setAmount(0);
                inventory.remove(itemStack);
                fixedIllegals++;
            }
        }

        if (Config.REMOVE_BOOKS) {
            // Remove books
            if (bookItemStacks.size() > Config.MAX_BOOKS) {
                for (ItemStack itemStack : bookItemStacks) {
                    inventory.remove(itemStack);
                    fixedBooks++;

                    if (Config.DROP_BOOKS) {
                        if (location != null) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    try {
                                        location.getWorld().dropItem(location, itemStack).setPickupDelay(20 * 5);
                                    } catch (NullPointerException exception) {
                                        cancel();
                                    }
                                }
                            }.runTaskLater(this, 0);
                        } else {
                            log("checkInventory", "Found too many books in shulker but could not find location to drop them.");
                        }
                    }
                }
            }
        }

        if (Config.LIMIT_IN_SHULKERS) {
            limitBlocksInShulker.size();
            for (Map.Entry<Material, List<ItemStack>> entry : limitBlocksInShulker.entrySet()) {
                int max = MaterialSets.limitedInShulkers.get(entry.getKey());
                int current = 0;

                for (ItemStack stack : entry.getValue()) {
                    if (current > max) {
                        inventory.remove(stack);
                        continue;
                    }

                    int newCurrent = current + stack.getAmount();

                    if (newCurrent > max) {
                        stack.setAmount(max - newCurrent);
                    }

                    current = newCurrent;
                }
            }
        }

        // Log
        if (wasFixed || fixedIllegals > 0 || fixedBooks > 0) {
            String message = String.format("Illegal Blocks: %s - %s Books: %s - Wrong Enchants: %s Limited: %s", fixedIllegals, Config.DROP_BOOKS ? "Dropped": "Deleted", fixedBooks, wasFixed, limited);
            log("checkInventory", message);
        }
    }

    /**
     * Check an item and try to fix it. If it is an illegal item, then remove it.
     *
     * @param itemStack      Item
     * @param location       Location for item drops
     * @param checkRecursive True, if inventories of containers should be checked
     * @return State of the Item
     */
    public ItemState checkItemStack(ItemStack itemStack, Location location, boolean checkRecursive) {
        boolean wasFixed = false;

        // null Item
        if (itemStack == null) return ItemState.EMPTY;

        if (Config.REMOVE_COLOR) {
            // Name Color Check
            if (itemStack.getType() != Material.WRITTEN_BOOK) {
                if (itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.stripColor(itemMeta.getDisplayName()));
                    itemStack.setItemMeta(itemMeta);
                }
            }
        }

        if (Config.FIX_UNBREAKABLE) {
            // Unbreakables
            if (itemStack.getType().isItem() && !itemStack.getType().isEdible() && !itemStack.getType().isBlock()) {
                if (itemStack.getDurability() > itemStack.getType().getMaxDurability() || itemStack.getDurability() < 0 || itemStack.getItemMeta().isUnbreakable()) {
                    itemStack.setDurability((short) 0);
                    itemStack.getItemMeta().setUnbreakable(false);
                    itemStack.setAmount(0);
                }
            }
        }

        if (Config.REMOVE_ILLEGALS) {
            // Illegal Blocks
            if (Checks.isIllegalBlock(itemStack.getType())) {
                itemStack.setAmount(0);
                return ItemState.ILLEGAL;
            }
        }

        if (Config.REVERT_FURNACE) {
            // nbt furnace check
            if (itemStack.getType() == Material.FURNACE && itemStack.toString().contains("internal=")) {
                // TODO: replace this hack with a solution that checks the nbt tag
                itemStack.setAmount(0);
                return ItemState.ILLEGAL;
            }
        }

        if (Config.REMOVE_OVER_STACKED) {
            // Revert Overstacked Items
            if (itemStack.getAmount() > itemStack.getMaxStackSize()) {
                itemStack.setAmount(itemStack.getMaxStackSize());
                wasFixed = true;
            }
        }

        if (Config.REMOVE_LORE) {
            // Check items with lore
            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasLore()) {
                // Christmas Illegals
            /*if (itemStack.getItemMeta().getLore().contains("Christmas Advent Calendar 2020"))
                return ItemState.clean;*/

                // Thunderclouds Item
                if (itemStack.getItemMeta().getLore().contains("ThunderCloud's Happy Little Friend. :)")) {
                    itemStack.setAmount(0);
                    return ItemState.ILLEGAL;
                }
            }
        }

        if (Config.REVERT_ENCHANTMENTS_CONFLICTING) {
            // Conflicting enchantments
            // We need to check if the enchantment we are checking for conflicts is the same as the one we are checking as it will conflict with itself
            if (Checks.isArmor(itemStack) || Checks.isWeapon(itemStack)) {
                // shuffle key set for random over enchantment removal
                List<Enchantment> keys = new ArrayList<>(itemStack.getEnchantments().keySet());
                Collections.shuffle(keys);

                // no for each loop to prevent concurrent modification exceptions
                for (int kI1 = 0; kI1 < keys.size(); kI1++) {
                    for (int kI2 = kI1 + 1; kI2 < keys.size(); kI2++) {
                        Enchantment e1 = keys.get(kI1);

                        if (e1.conflictsWith(keys.get(kI2))) {
                            itemStack.removeEnchantment(e1);
                            //log("checkItem", "Removing conflicting enchantment " + e1.getName() + " from " + itemStack.getType());
                            keys.remove(e1);
                            if (kI1 > 0) {
                                // check next item
                                kI1--;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (Config.REVERT_ENCHANTMENTS_MAX) {
            // Max Enchantment
            for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                if (!enchantment.canEnchantItem(itemStack) && !Checks.isArmor(itemStack) && !Checks.isWeapon(itemStack)
                        && itemStack.getEnchantmentLevel(enchantment) > maxLoreEnchantmentLevel) {
                    // enforce lore enchantments level
                    wasFixed = true;

                    itemStack.removeEnchantment(enchantment);
                    itemStack.addUnsafeEnchantment(enchantment, maxLoreEnchantmentLevel);
                } else if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                    // enforce max enchantment level
                    wasFixed = true;

                    itemStack.removeEnchantment(enchantment);
                    itemStack.addEnchantment(enchantment, enchantment.getMaxLevel());
                }
            }
        }

        if (Config.CHECK_SHULKERS) {
            // ShulkerBox Check
            if (itemStack.getType().toString().contains("SHULKER_BOX")) {
                if (checkRecursive && itemStack.getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta blockMeta = (BlockStateMeta) itemStack.getItemMeta();

                    if (blockMeta.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) blockMeta.getBlockState();

                        Inventory inventoryShulker = shulker.getInventory();

                        checkInventory(inventoryShulker, location, true, true);

                        shulker.getInventory().setContents(inventoryShulker.getContents());
                        blockMeta.setBlockState(shulker);

                        // JsonParseException
                        try {
                            itemStack.setItemMeta(blockMeta);
                        } catch (Exception e) {
                            log("checkItem", "Exception " + e.getMessage());
                        }
                    }
                }
            }
        }

        if (Config.MAX_BOOKS_IN_SHULKER) {
            // books
            if (itemStack.getType() == Material.WRITTEN_BOOK || itemStack.getType() == Material.BOOK_AND_QUILL)
                return ItemState.WRITTEN_BOOK;
        }

        if (Config.LIMIT_IN_SHULKERS) {
            if (MaterialSets.limitedInShulkers.containsKey(itemStack.getType())) {
                return ItemState.LIMITED_IN_SHULKERS;
            }
        }

        return wasFixed ? ItemState.WAS_FIXED : ItemState.CLEAN;
    }

    public void log(String module, String message) {
        getLogger().info("§a[" + module + "] §e" + message + "§r");
    }

    /**
     * fired when the plugin gets enabled
     */
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        Config.load(config);
        MaterialSets.load(config);
        getServer().getPluginManager().registerEvents(new Events(this), this);
        log("onEnable", "");
    }

    public enum ItemState {
        EMPTY, CLEAN, WAS_FIXED, ILLEGAL, WRITTEN_BOOK, LIMITED_IN_SHULKERS
    }
}