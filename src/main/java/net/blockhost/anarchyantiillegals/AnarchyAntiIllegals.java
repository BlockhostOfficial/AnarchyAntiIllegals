package net.blockhost.anarchyantiillegals;

import com.cryptomorin.xseries.XMaterial;
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
import java.util.concurrent.atomic.AtomicReference;

public class AnarchyAntiIllegals extends JavaPlugin {
    private static final int MAX_LORE_ENCHANTMENT_LEVEL = 1;

    public void checkInventory(Inventory inventory, Location location, boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    /**
     * use this method to check and remove illegal items from inventories
     *
     * @param inventory      the inventory that should be checked
     * @param location       location of the inventory holder for possible item drops
     * @param checkRecursive true, if items inside containers should be checked
     * @return true, if the inventory was modified
     */
    public boolean checkInventory(Inventory inventory, Location location, boolean checkRecursive, boolean isInsideShulker) {
        isInsideShulker = isInsideShulker || inventory.getHolder() instanceof ShulkerBox;

        List<ItemStack> removeItemStacks = new ArrayList<>();
        List<ItemStack> bookItemStacks = new ArrayList<>();
        Map<Material, List<Integer>> limitBlocksInShulker = new EnumMap<>(Material.class);

        boolean wasFixed = false;
        int fixedIllegals = 0;
        int fixedBooks = 0;
        int limited = 0;

        int index = 0;
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
                    if (isInsideShulker) {
                        bookItemStacks.add(itemStack);
                    }
                    break;

                case LIMITED_IN_SHULKERS:
                    if (isInsideShulker) {
                        limitBlocksInShulker.computeIfAbsent(itemStack.getType(), k -> new ArrayList<>()).add(index);
                    }
                    break;

                default:
                    break;
            }
            index++;
        }

        if (Config.REMOVE_ILLEGALS) {
            // Remove illegal items
            for (ItemStack itemStack : removeItemStacks) {
                if (!Config.ONLY_LOG) {
                    itemStack.setAmount(0);
                }
                fixedIllegals++;
            }
        }

        if (Config.REMOVE_BOOKS) {
            // Remove books
            if (bookItemStacks.size() > Config.MAX_BOOKS) {
                for (ItemStack itemStack : bookItemStacks) {
                    if (!Config.ONLY_LOG) {
                        itemStack.setAmount(0);
                    }

                    fixedBooks++;

                    if (!Config.ONLY_LOG && Config.DROP_BOOKS) {
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
            for (Map.Entry<Material, List<Integer>> entry : limitBlocksInShulker.entrySet()) {
                int max = MaterialSets.LIMITED_IN_SHULKERS.get(entry.getKey());
                int current = 0;

                for (Integer stack : entry.getValue()) {
                    current++;
                    if (current > max) {
                        if (!Config.ONLY_LOG) {
                            inventory.clear(stack);
                        }

                        limited++;
                    }
                }
            }
        }

        // Log
        if (wasFixed || fixedIllegals > 0 || fixedBooks > 0 || limited > 0) {
            String message = String.format("Illegal Blocks: %s - %s Books: %s - Wrong Enchants: %s Limited: %s", fixedIllegals, Config.DROP_BOOKS ? "Dropped" : "Deleted", fixedBooks, wasFixed, limited);
            log("checkInventory", message);
            return true;
        } else return false;
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
        AtomicReference<ItemMeta> metaRef = new AtomicReference<>();

        // null Item
        if (itemStack == null) return ItemState.EMPTY;

        if (Config.REMOVE_COLOR) {
            // Name Color Check
            if (itemStack.getType() != XMaterial.WRITTEN_BOOK.parseMaterial()) {
                if (itemStack.hasItemMeta()) {
                    ItemMeta itemMeta = getMeta(metaRef, itemStack);
                    if (itemMeta.hasDisplayName()) {
                        String name = itemMeta.getDisplayName();
                        String stripped = ChatColor.stripColor(name);
                        if (!name.equals(stripped)) {
                            if (!Config.ONLY_LOG) {
                                itemMeta.setDisplayName(stripped);
                                itemStack.setItemMeta(itemMeta);
                            }
                            wasFixed = true;
                        }
                    }
                }
            }
        }

        if (Config.FORCE_ASCII_DISPLAY_NAME) {
            if (itemStack.hasItemMeta() && getMeta(metaRef, itemStack).hasDisplayName()) {
                ItemMeta itemMeta = getMeta(metaRef, itemStack);
                String displayName = itemMeta.getDisplayName();

                if (displayName.matches("[^\\p{ASCII}]")) {
                    if (!Config.ONLY_LOG) {
                        itemMeta.setDisplayName(displayName.replaceAll("[^\\p{ASCII}]", ""));
                        itemStack.setItemMeta(itemMeta);
                    }
                    wasFixed = true;
                }
            }
        }

        if (Config.FIX_UNBREAKABLE) {
            // Unbreakables
            if (itemStack.getType().isItem() && !itemStack.getType().isEdible() && !itemStack.getType().isBlock()) {
                if (itemStack.getDurability() > itemStack.getType().getMaxDurability() || itemStack.getDurability() < 0 || getMeta(metaRef, itemStack).isUnbreakable()) {
                    ItemMeta itemMeta = getMeta(metaRef, itemStack);
                    if (!Config.ONLY_LOG) {
                        itemStack.setDurability((short) 0);
                        itemMeta.setUnbreakable(false);
                        itemStack.setItemMeta(itemMeta);
                        itemStack.setAmount(0);
                    }
                }
            }
        }

        if (Config.REMOVE_ILLEGALS) {
            // Illegal Blocks
            if (Checks.isIllegalBlock(itemStack.getType())) {
                if (!Config.ONLY_LOG) {
                    itemStack.setAmount(0);
                }

                return ItemState.ILLEGAL;
            }
        }

        if (Config.REVERT_FURNACE) {
            // nbt furnace check
            if (itemStack.getType() == XMaterial.FURNACE.parseMaterial() && itemStack.toString().contains("internal=")) {
                // TODO: replace this hack with a solution that checks the nbt tag
                if (!Config.ONLY_LOG) {
                    itemStack.setAmount(0);
                }
                return ItemState.ILLEGAL;
            }
        }

        if (Config.REMOVE_OVER_STACKED) {
            // Revert Overstacked Items
            if (itemStack.getAmount() > itemStack.getMaxStackSize()) {
                if (!Config.ONLY_LOG) {
                    itemStack.setAmount(itemStack.getMaxStackSize());
                }

                wasFixed = true;
            }
        }

        if (Config.REMOVE_LORE) {
            // Check items with lore
            if (itemStack.hasItemMeta() && getMeta(metaRef, itemStack).hasLore()) {
                if (!Config.ONLY_LOG) {
                    itemStack.setAmount(0);
                }

                return ItemState.ILLEGAL;
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
                            if (!Config.ONLY_LOG) {
                                itemStack.removeEnchantment(e1);
                            }

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
                        && itemStack.getEnchantmentLevel(enchantment) > MAX_LORE_ENCHANTMENT_LEVEL) {
                    // enforce lore enchantments level
                    wasFixed = true;

                    if (!Config.ONLY_LOG) {
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, MAX_LORE_ENCHANTMENT_LEVEL);
                    }
                } else if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                    // enforce max enchantment level
                    wasFixed = true;

                    if (!Config.ONLY_LOG) {
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addEnchantment(enchantment, enchantment.getMaxLevel());
                    }
                }
            }
        }

        if (Config.CHECK_SHULKERS) {
            // ShulkerBox Check
            if (itemStack.getType().toString().contains("SHULKER_BOX")) {
                if (checkRecursive && getMeta(metaRef, itemStack) instanceof BlockStateMeta) {
                    BlockStateMeta blockMeta = (BlockStateMeta) getMeta(metaRef, itemStack);

                    if (blockMeta.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) blockMeta.getBlockState();

                        Inventory inventoryShulker = shulker.getInventory();

                        boolean wasChanged = checkInventory(inventoryShulker, location, true, true);

                        if (wasChanged && !Config.ONLY_LOG) {
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
        }

        if (Config.MAX_BOOKS_IN_SHULKER) {
            // books
            if (itemStack.getType() == XMaterial.WRITTEN_BOOK.parseMaterial() || itemStack.getType() == XMaterial.WRITABLE_BOOK.parseMaterial())
                return ItemState.WRITTEN_BOOK;
        }

        if (Config.LIMIT_IN_SHULKERS) {
            if (MaterialSets.LIMITED_IN_SHULKERS.containsKey(itemStack.getType())) {
                return ItemState.LIMITED_IN_SHULKERS;
            }
        }

        return wasFixed ? ItemState.WAS_FIXED : ItemState.CLEAN;
    }

    private ItemMeta getMeta(AtomicReference<ItemMeta> reference, ItemStack dataSource) {
        ItemMeta referenceResolved = reference.get();
        if (referenceResolved == null) {
            ItemMeta createdMeta = dataSource.getItemMeta();
            reference.set(createdMeta);
            return createdMeta;
        } else return referenceResolved;
    }

    public void log(String module, String message) {
        getLogger().info(() -> String.format("§a[%s] §e%s§r", module, message));
    }

    /**
     * fired when the plugin gets enabled
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        Config.load(config);
        MaterialSets.load(config, this);
        getServer().getPluginManager().registerEvents(new Events(this), this);
        log("onEnable", "");
    }

    public enum ItemState {
        EMPTY, CLEAN, WAS_FIXED, ILLEGAL, WRITTEN_BOOK, LIMITED_IN_SHULKERS
    }
}
