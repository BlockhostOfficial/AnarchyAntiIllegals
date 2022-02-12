package net.blockhost.anarchyantiillegals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Events implements Listener {
    private final AnarchyAntiIllegals plugin;

    public Events(AnarchyAntiIllegals plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!Config.EVENT_BLOCK_BREAK) return;

        if (!(event.getBlock().getState() instanceof InventoryHolder)) return;

        // inventory of the block
        Inventory inventory = ((InventoryHolder) event.getBlock().getState()).getInventory();
        Location location = event.getBlock().getLocation();

        plugin.checkInventory(inventory, location, true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (!Config.EVENT_BLOCK_PLACE) return;

        if (Config.REMOVE_ILLEGALS) {
            // placed block - stop placing if it's an illegal
            if (Checks.isIllegalBlock(event.getBlockPlaced().getType())
                    && event.getBlockPlaced().getType() != event.getBlockReplacedState().getType()) {
                event.setCancelled(true);
                plugin.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing " + event.getBlockPlaced());
            }
        }

        plugin.checkItemStack(event.getItemInHand(), event.getPlayer().getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!Config.EVENT_VEHICLE_DESTROY) return;

        if (event.getVehicle() instanceof InventoryHolder) {
            // inventory of the vehicle
            Inventory inventory = ((InventoryHolder) event.getVehicle()).getInventory();
            Location location = event.getVehicle().getLocation();

            plugin.checkInventory(inventory, location, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        if (!Config.EVENT_PLAYER_ITEM_DROP) return;

        if (event.getItemDrop() == null || event.getItemDrop().getItemStack() == null)
            return;

        plugin.checkItemStack(event.getItemDrop().getItemStack(), event.getItemDrop().getLocation(), true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!Config.EVENT_ENTITY_PICKUP_ITEM) return;

        if (event.getItem() == null || event.getItem().getItemStack() == null)
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (plugin.checkItemStack(event.getItem().getItemStack(), player.getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL) {
            event.setCancelled(true);
            plugin.log(event.getEventName(), "Stopped " + event.getEntity().getName() + " from picking up an illegal item");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!Config.EVENT_ENTITY_DEATH) return;

        if (event.getDrops() == null || event.getDrops().isEmpty()) return;

        for (ItemStack drop : event.getDrops()) {
            plugin.checkItemStack(drop, event.getEntity().getLocation(), false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (!Config.EVENT_PLAYER_SWAP_HAND_ITEMS) return;

        if (event.getMainHandItem() != null
                && plugin.checkItemStack(event.getMainHandItem(), event.getPlayer().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);

        if (event.getOffHandItem() != null
                && plugin.checkItemStack(event.getOffHandItem(), event.getPlayer().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (!Config.EVENT_PLAYER_ITEM_HELD) return;

        if (event.getPlayer().getInventory() == null)
            return;

        if (event.getPlayer().getInventory().getItem(event.getNewSlot()) != null)
            if (plugin.checkItemStack(event.getPlayer().getInventory().getItem(event.getNewSlot()), event.getPlayer().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
                event.setCancelled(true);

        if (event.getPlayer().getInventory().getItem(event.getPreviousSlot()) != null)
            if (plugin.checkItemStack(event.getPlayer().getInventory().getItem(event.getPreviousSlot()), event.getPlayer().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
                event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (!Config.EVENT_INVENTORY_MOVE_ITEM) return;

        if (event.getItem() == null) return;

        if (plugin.checkItemStack(event.getItem(), event.getSource().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);
    }

    @SuppressWarnings("IsCancelled")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!Config.EVENT_PLAYER_INTERACT_ENTITY) return;

        if (event.getRightClicked() == null) return;

        // Item Frame check only
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        Location loc = event.getPlayer().getLocation();
        PlayerInventory inv = event.getPlayer().getInventory();

        if (plugin.checkItemStack(inv.getItemInMainHand(), loc, false) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);

        if (plugin.checkItemStack(inv.getItemInOffHand(), loc, false) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);

        ItemStack frameStack = ((ItemFrame) event.getRightClicked()).getItem();

        if (plugin.checkItemStack(frameStack, loc, false) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);

        if (event.isCancelled())
            plugin.log(event.getEventName(), "Stopped " + event.getPlayer().getName() + " from placing an illegal item in an item frame");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        if (!Config.EVENT_HANGING_BREAK) return;

        if (event.getEntity() == null) return;
        if (!(event.getEntity() instanceof ItemFrame)) return;

        ItemStack item = ((ItemFrame) event.getEntity()).getItem();

        if (plugin.checkItemStack(item, event.getEntity().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL) {
            event.setCancelled(true);
            ((ItemFrame) event.getEntity()).setItem(new ItemStack(Material.AIR));
            plugin.log(event.getEventName(), "Deleted Illegal from " + event.getEntity().getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!Config.EVENT_ENTITY_DAMAGE_BY_ENTITY) return;

        // only if an item frame get hit
        if (!(event.getEntity() instanceof ItemFrame)) return;

        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        if (plugin.checkItemStack(itemFrame.getItem(), event.getEntity().getLocation(), false) == AnarchyAntiIllegals.ItemState.ILLEGAL) {
            itemFrame.setItem(new ItemStack(Material.AIR));
            plugin.log(event.getEventName(), "Removed illegal item from " + itemFrame);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!Config.EVENT_INVENTORY_CLICK) return;

        if (event.getClickedInventory() == null) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        if (plugin.checkItemStack(event.getCurrentItem(), event.getWhoClicked().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);

        if (plugin.checkItemStack(event.getCursor(), event.getWhoClicked().getLocation(), true) == AnarchyAntiIllegals.ItemState.ILLEGAL)
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!Config.EVENT_INVENTORY_OPEN) return;

        if (event.getInventory().equals(event.getPlayer().getEnderChest())) return;

        plugin.checkInventory(event.getInventory(), event.getPlayer().getLocation(), true);
    }

    // from cloudanarchy core
    // dropper / dispenser
    // This event does not get canceled on purpose because the item handling on event cancel is so wonky!
    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!Config.EVENT_BLOCK_DISPENSE) return;

        if (plugin.checkItemStack(event.getItem(), event.getBlock().getLocation(), false) == AnarchyAntiIllegals.ItemState.ILLEGAL) {
            event.setCancelled(true);
            event.setItem(new ItemStack(Material.AIR));
            event.getBlock().getState().update(true, false);
            plugin.log(event.getEventName(), "Stopped dispensing of an illegal block.");
        }
    }
}
