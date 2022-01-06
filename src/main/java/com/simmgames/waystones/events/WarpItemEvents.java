package com.simmgames.waystones.events;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.Config;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.items.WarpCrystal;
import com.simmgames.waystones.items.WarpItem;
import com.simmgames.waystones.items.WarpScroll;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.util.Default;
import com.simmgames.waystones.util.Work;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.logging.Logger;

public class WarpItemEvents implements Listener {
    Logger out;
    Data data;
    Server server;
    WaystoneBlockEvents waystoneEvents;

    Map<String, BukkitTask> InventoryUpdates;

    public WarpItemEvents(Logger logger, Data pluginData, Server server, WaystoneBlockEvents waystoneEvents)
    {
        out = logger;
        data = pluginData;
        this.waystoneEvents = waystoneEvents;
        this.server = server;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        Player p = event.getPlayer();

        ItemStack item = event.getItem();
        if(item == null)
            return;
        WarpItem warpItem= null;

        if(event.getHand() == EquipmentSlot.OFF_HAND)
        {
            ItemStack mainHand = p.getInventory().getItemInMainHand();
            // ignore if warp item is in main Hand
            if(mainHand != null)
                if(mainHand.getType() != Material.AIR)
                    if(WarpItem.isWarpItem(mainHand))
                        return;
        }

        if(WarpItem.isWarpItem(item))
        {
            warpItem = new WarpItem(item);
        }
        if(warpItem == null)
            return;

        // Set waystone
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.LODESTONE)
        {
            // Check if lodestone
            Waystone waystone = Work.GetWaystoneAt(event.getClickedBlock().getLocation(), data);

            //if it, set the item to this waystone
            if(waystone != null)
            {
                // Permission Check
                if(p.hasPermission(Perm.MakeWayItem)) {
                    // Private check
                    if(waystone.access != Accessibility.Private || waystone.owner.equalsIgnoreCase(p.getUniqueId().toString())){
                        ItemStack NewItem = warpItem.SetWaystone(waystone.uuid, waystone.decodeNameNoUseState(data));
                        WarpItem newWarp = new WarpItem(NewItem);
                        if (WarpCrystal.IsWarpCrystal(newWarp.item)) {
                            newWarp.AppendLore(WarpCrystal.ExtraLore(newWarp.item));
                            newWarp.SetNotStackable();
                        }
                        // Give new item to player
                        Map<Integer, ItemStack> couldNotFit = p.getInventory().addItem(newWarp.item);
                        for (Map.Entry<Integer, ItemStack> kvp : couldNotFit.entrySet()) {
                            p.getWorld().dropItem(p.getLocation().add(0, 1, 0), kvp.getValue());
                        }
                        // Effects?

                        if (WarpCrystal.IsWarpCrystal(newWarp.item))
                            p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_STEP, SoundCategory.PLAYERS, 1f, 2f);
                        else if (WarpScroll.IsWarpScroll(newWarp.item))
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, SoundCategory.PLAYERS, 1f, 1.5f);
                        else
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1f, 2f);
                        return;
                    } else {
                        p.sendMessage(ChatColor.RED + "You can't make a WayItem of a Private Waystone that you don't own.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "You don't have permission to make a WayItem.");
                }
            }
        }

        // don't use items if using a block
        if(event.getClickedBlock() != null)
        {
            if(Work.IsIntractable(event.getClickedBlock().getType()))
            {
                return;
            }
        }


        // Use Item
        if(!warpItem.waystoneUUID.equalsIgnoreCase(Default.UUIDZero))
        {
            Waystone waystone = data.WaystoneFromUUID(warpItem.waystoneUUID);
            boolean waystoneDNE = false;

            // Check permissions
            if(!p.hasPermission(Perm.UseWayItem))
            {
                p.sendMessage(ChatColor.RED + "You don't have permission to use a WayItem.");
                return;
            }

            if(waystone != null) {
                if(waystone.canUse() && waystone.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString())) {
                    double chance = 1;
                    if(WarpCrystal.IsWarpCrystal(item)) {
                        chance = WarpCrystal.GetChanceRate(warpItem.item);
                        WarpCrystal.Used(warpItem.item);
                        warpItem.ModifyLore(2, WarpCrystal.ExtraLore(warpItem.item));
                    }
                    warpItem.RemoveItem(chance);

                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1, 2);
                    waystoneEvents.OnTeleport(p, waystone, null);
                    return;
                }
            } else {
                waystoneDNE = true;
            }
            // Event failed. Waystone DNE or Unavailable
            p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, p.getLocation().add(0, 1, 0), 50, 0.5, 1, 0.5);

            if(waystoneDNE)
            {
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_CANDLE_EXTINGUISH, SoundCategory.PLAYERS, 1, 2);
                ItemStack newItem = warpItem.Unlink();
                WarpItem wp = new WarpItem(newItem);
                if(WarpCrystal.IsWarpCrystal(wp.item))
                    wp.AppendLore(WarpCrystal.ExtraLore(wp.item));


                Map<Integer, ItemStack> couldNotFit = p.getInventory().addItem(wp.item);
                for(Map.Entry<Integer, ItemStack> kvp: couldNotFit.entrySet())
                {
                    p.getWorld().dropItem(p.getLocation().add(0, 1, 0), kvp.getValue());
                }
            } else {
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1, 2);
            }

            if(Config.UseWarpItemOnFail())
            {
                double chance = 1;
                if(WarpCrystal.IsWarpCrystal(item)) {
                    chance = WarpCrystal.GetChanceRate(warpItem.item);
                    WarpCrystal.Used(warpItem.item);
                    warpItem.AppendLore(WarpCrystal.ExtraLore(warpItem.item));
                }
                warpItem.RemoveItem(chance);
            }
        }
    }

    @EventHandler
    public void OnPrepareEvent(PrepareResultEvent event)
    {
        ItemStack item = event.getResult();
        if(item != null)
            if(WarpItem.isWarpItem(item))
            {
                WarpItem warpItem = new WarpItem(item);
                if(warpItem.waystoneUUID.equalsIgnoreCase(Default.UUIDZero))
                    return;
                event.setResult(null);
            }
    }
}
