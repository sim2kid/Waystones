package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.structure.Vector3;
import com.simmgames.waystones.util.Work;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaystoneBlockEvents implements Listener
{
    Logger out;
    Data data;
    Server server;

    public WaystoneBlockEvents(Logger logger, Data pluginData, Server server)
    {
        out = logger;
        data = pluginData;
        this.server = server;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(!isLodestone(event))
            return;
        Player p = event.getPlayer();
        // Ask to create Waystone
        p.sendMessage("If you would like to make this a Waystone, run\n/waystone create <name>");
        // Wait for command on creation.
    }

    @EventHandler
    public void onBlockTouch(BlockDamageEvent event)
    {
        if(!isLodestone(event))
            return;

        Player p = event.getPlayer();

        // check if it is a waystone
        Waystone wei = GetWaystoneAt(event.getBlock().getLocation());
        if(wei == null)
        {
            p.sendMessage("This Lodestone is currently not a waystone. If you would like to make it a Waystone, please run \n/waystone create <name>");
            return;
        }


        // check if player owns waystone
        // IF player owns waystone, let them know they are about to destroy the waystone
        if(wei.owner == p.getUniqueId().toString())
        {
            p.sendMessage("You're about to break your Waystone.");
        }
        // ELSE let the player know who owns the waystone and what it's name is
        TouchWaystone(p, wei);
        // Plus tell the player if they discovered the waystone

        // If not a waystone, ask player if they want to create a waystone
        // then wait for the creation command
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if(event.getClickedBlock().getBlockData().getMaterial() != Material.LODESTONE)
            return;
        if(event.getHand() != EquipmentSlot.HAND)
            return;

        Player p = event.getPlayer();

        Waystone wei = GetWaystoneAt(event.getClickedBlock().getLocation());
        if(wei == null) {
            out.log(Level.INFO, "Waystone not found");
            return;
        }
        TouchWaystone(p, wei);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(!isLodestone(event))
            return;

        Player p = event.getPlayer();

        // Check if it is a waystone
        Waystone wei = GetWaystoneAt(event.getBlock().getLocation());
        if(wei == null)
            return;
        // If it is a waystone, check if it's owned by the player initiating the command
        if(!wei.owner.equalsIgnoreCase(p.getUniqueId().toString()))
        {
            p.sendMessage("You must own this Waystone to break it.");
            event.setCancelled(true);
            return;
        }

        DestroyWaystone(event.getBlock().getLocation());
        // If it is, notify player that they destroyed the waystone
        // Update waystone data
        // Update all online player's data
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        List<Block> lodes = getLodestones(event.blockList());
        for (Block lode: lodes)
        {
            OnExploded(lode);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event)
    {
        List<Block> lodes = getLodestones(event.blockList());
        for (Block lode: lodes)
        {
            OnExploded(lode);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player p = event.getPlayer();

        // Check for waystones near the player
        // Update whether the player is in a waystone or not
        GetClosestWaystone(p);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();

        // Load the player's data into the program
        WayPlayer player = data.GrabPlayer(p.getUniqueId().toString());

        // Update player's known Waystones list.
        UpdatePlayerInformation(p);

        // Check for waystones near the player
        // Update whether the player is in a waystone or not
        GetClosestWaystone(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();

        UpdatePlayerInformation(p);
        // Retire the player's data from the program
        data.RetirePlayer(p.getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        // Check if player is near a waystone or not.
        GetClosestWaystone(player);
    }

    private void OnExploded(Block b)
    {
        DestroyWaystone(b.getLocation());
    }

    private void TouchWaystone(Player player, Waystone wei)
    {
        if(!Work.UpdateHologram(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), wei.decodeName(data)))
        {
            wei.hologramUUID = Work.CreateHologram(wei.location.getLocation(server), wei.decodeName(data)).toString();
        }
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        if(!DiscoverWaystone(player, wei))
        {
            Title(player,"", wei.decodeName(data));
            player.sendMessage(wei.decodeName(data));
        }
    }

    private void DestroyWaystone(Location location)
    {
        // Look for waystone at location in world
        Waystone wei = GetWaystoneAt(location);
        if(wei == null)
            return;
        Work.DestroyHologram(location, UUID.fromString(wei.hologramUUID));
        // delete waystone in the Waystone list
        data.AllWaystones.remove(wei);
        // Delete waystone in all "known waystones" for online players
        UpdateOnlinePlayers();
        data.Save();
    }

    private void UpdateOnlinePlayers()
    {
        for(Player p: server.getOnlinePlayers())
        {
            UpdateWaystones(p);
        }
    }

    private void UpdateWaystones(Player player)
    {
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        // Update a player's known waystone list (remove bad waystones)

        // If waystones are missing, let player know that they have been destroyed.
        // If player's owned waystones are missing, let them know that they have been destroyed
        for (int i = p.KnownWaystones.size()-1; i >= 0 ; i--)
        {
            Waystone known = p.KnownWaystones.get(i);
            int index = data.AllWaystones.indexOf(known);
            if(index != -1)
            {
                p.KnownWaystones.set(i, data.AllWaystones.get(index));
            }
            else
            {
                if(known.owner.equalsIgnoreCase(p.UUID))
                {
                    SendPlayerMessage(player, "Your Waystone '" + known.name + "' at '"
                            + known.location.toString() + "' has been destroyed.");
                } else {
                    SendPlayerMessage(player, "Waystone '" + known.name + "' built by '"
                            + data.GrabPlayer(known.owner).lastUsername + "' at '"
                            + known.location.toString() + "' has been destroyed.");
                }
                p.KnownWaystones.remove(i);
            }
        }
    }

    private void SendPlayerMessage(Player player, String message)
    {
        player.sendMessage(message);
    }

    private void UpdatePlayerInformation(Player player)
    {
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        // Update player's stored Username
        p.lastUsername = player.getName();
        // Update player's waystone list.
        UpdateWaystones(player);

        data.SavePlayer(p.UUID);
    }

    private Waystone GetClosestWaystone(Player player)
    {
        Waystone closest = null;
        double closestDistance = -1;

        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());

        for(Waystone wei : data.AllWaystones)
        {
            double distance = wei.location.getDistance(new BlockLocation(player.getLocation(), true));
            if(distance == -1)
            {
                // In another world
                continue;
            }

            // Within waystone use distance
            if (closestDistance == -1 || distance < closestDistance) {
                closestDistance = distance;
                closest = wei;
            }
        }



        if(closest != null) {
            Block block = player.getWorld().getBlockAt(new Location(player.getWorld(), (int) closest.location.getX(),
                    (int) closest.location.getY(), (int) closest.location.getZ()));
            if(block != null)
                if (block.getType() != Material.LODESTONE)
                {
                    closest = null;
                    DestroyWaystone(block.getLocation());
                    return closest;
                }

            if (closestDistance != -1 && closestDistance < data.WaystoneUseDistance()) {
                if (p.InWaystoneUse != true)
                    OnUseEnter(player, closest);
                p.InWaystoneUse = true;
            } else {
                if (p.InWaystoneUse != false)
                    OnUseExit(player, closest);
                p.InWaystoneUse = false;
            }
            if (closestDistance != -1 && closestDistance < data.WaystoneDiscoverDistance()) {
                if (p.InWaystoneDiscover != true) {
                    OnDiscoverEnter(player, closest);
                }
                p.InWaystoneDiscover = true;
                // Waystone is now discovered
                DiscoverWaystone(player, closest);
            } else {
                if (p.InWaystoneDiscover != false)
                    OnDiscoverExit(player, closest);
                p.InWaystoneDiscover = false;
            }
        }
        if(!Work.UpdateHologram(closest.location.getLocation(server), UUID.fromString(closest.hologramUUID), closest.decodeName(data)))
        {
            closest.hologramUUID = Work.CreateHologram(closest.location.getLocation(server), closest.decodeName(data)).toString();
        }

        p.LastVisited = closest;
        return closest;
    }

    public Waystone GetWaystoneAt(Location location)
    {
        for(Waystone wei : data.AllWaystones)
        {
            if(!wei.location.WorldUUID.equalsIgnoreCase(location.getWorld().getUID().toString()))
                continue;
            if(wei.location.Position.equals(new Vector3(location)))
                return wei;
        }
        return null;
    }

    private void OnDiscoverEnter(Player player, Waystone waystone)
    {
        Title(player,"Near Waystone", waystone.decodeName(data));
    }
    private void OnDiscoverExit(Player player, Waystone waystone)
    {

    }
    private void OnUseEnter(Player player, Waystone waystone)
    {

    }
    private void OnUseExit(Player player, Waystone waystone)
    {

    }
    public boolean DiscoverWaystone(Player player, Waystone waystone)
    {
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        if(p.KnownWaystones.contains(waystone))
            return false;
        p.KnownWaystones.add(waystone);
        data.SavePlayer(p.UUID);
        player.resetTitle();
        player.getLocation().getWorld().playSound(player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 8.0f, 1.0f);
        Title(player,"Waystone Discovered", waystone.decodeName(data));
        return true;
    }
    public void OnCreateWaystone(Player player, Waystone waystone)
    {
        player.resetTitle();
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        p.KnownWaystones.add(waystone);
        player.getLocation().getWorld().playSound(player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 8.0f, 1.0f);
        Title(player,"Waystone Created", waystone.decodeName(data));
        data.Save();
    }

    private boolean isLodestone(BlockEvent event) {
        return event.getBlock().getBlockData().getMaterial() == Material.LODESTONE;
    }

    private void Title(Player player, String title, String subtitle)
    {
        player.sendTitle(title, subtitle, 5, 50, 10);
    }

    private List<Block> getLodestones(List<Block> blockList)
    {
        List<Block> blocks = new ArrayList<Block>();
        for (Block b: blockList) {
            if(b.getBlockData().getMaterial() == Material.LODESTONE)
            {
                blocks.add(b);
            }
        }
        return blocks;
    }
}
