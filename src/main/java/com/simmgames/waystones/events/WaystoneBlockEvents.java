package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.structure.Vector3;
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
        p.sendMessage("You have placed a Lodestone.");
        // Wait for command on creation.
    }

    @EventHandler
    public void onBlockTouch(BlockDamageEvent event)
    {
        if(!isLodestone(event))
            return;

        Player p = event.getPlayer();
        p.sendMessage("You have touched a Lodestone.");

        // check if it is a waystone
        // check if player owns waystone
        // IF player owns waystone, let them know they are about to destroy the waystone
        // ELSE let the player know who owns the waystone and what it's name is
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
        p.sendMessage("You have interacted with a Lodestone.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(!isLodestone(event))
            return;

        Player p = event.getPlayer();
        p.sendMessage("You have broke a Lodestone.");

        // Check if it is a waystone
        // If it is a waystone, check if it's owned by the player initiating the command
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
        p.sendMessage("You started typing a command");

        // Check for waystones near the player
        // Update whether the player is in a waystone or not
        GetClosestWaystone(p);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();
        p.sendMessage("You have joined the server");

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
        out.log(Level.INFO, p.name().toString() + " has left the server, imo");

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
        out.log(Level.INFO, "Lodestone has Exploded!");
    }

    private void DestroyWaystone(Location location)
    {
        // Look for waystone at location in world
        // delete waystone in the Waystone list
        // Delete waystone in all "known waystones" for online players

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
                if(known.owner == p.UUID)
                {
                    SendPlayerMessage(player, "Your Waystone '" + known.name + "' at '"
                            + known.location.Formatted(server) + "' has been destroyed.");
                } else {
                    SendPlayerMessage(player, "Waystone '" + known.name + "' built by '" + known.owner + "' at '"
                            + known.location.Formatted(server) + "' has been destroyed.");
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
    }

    private Waystone GetClosestWaystone(Player player)
    {
        Waystone closest = null;
        double closestDistance = -1;

        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());

        for(Waystone wei : data.AllWaystones)
        {
            double distance = wei.location.getDistance(new BlockLocation(player.getLocation()));
            if(distance == -1)
            {
                // In another world
                continue;
            }

            if(distance < data.WaystoneUseDistance())
            {
                // Within waystone use distance
                if(closestDistance == -1 || distance < closestDistance) {
                    closestDistance = distance;
                    closest = wei;
                }

                if(p.InWaystoneUse != true)
                    OnUseEnter(player, wei);
                p.InWaystoneUse = true;
            } else {
                if(p.InWaystoneUse != false)
                    OnUseExit(player, wei);
                p.InWaystoneUse = false;
            }
            if(distance < data.WaystoneDiscoverDistance())
            {
                if(p.InWaystoneDiscover != true)
                {
                    OnDiscoverEnter(player, wei);
                }
                p.InWaystoneDiscover = true;
                // Waystone is now discovered
                if(!p.KnownWaystones.contains(wei))
                    DiscoverWaystone(player, wei);
            } else {
                if(p.InWaystoneDiscover != false)
                    OnDiscoverExit(player, wei);
                p.InWaystoneDiscover = false;
            }
        }
        p.LastVisited = closest;
        return closest;
    }

    private void OnDiscoverEnter(Player player, Waystone waystone)
    {
        player.sendTitle(Color.LIME + "Near New Waystone", Color.TEAL + waystone.decodeName(data), 1, 3, 1);
    }
    private void OnDiscoverExit(Player player, Waystone waystone)
    {

    }
    private void OnUseEnter(Player player, Waystone waystone)
    {
        player.sendTitle("", Color.BLUE + "Waystone is now Useable", 1, 3, 1);
    }
    private void OnUseExit(Player player, Waystone waystone)
    {
        player.sendTitle("", Color.RED + "Waystone is nolonger Useable", 1, 3, 1);
    }
    public void DiscoverWaystone(Player player, Waystone waystone)
    {
        player.resetTitle();
        waystone.location.getLocation(server).getWorld().playSound(player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 8.0f, 1.0f);
        player.sendTitle(Color.GREEN + "Waystone Discovered", Color.TEAL + waystone.decodeName(data), 1, 3, 1);
    }
    public void OnCreateWaystone(Player player, Waystone waystone)
    {
        player.resetTitle();
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        p.KnownWaystones.add(waystone);
        waystone.location.getLocation(server).getWorld().playSound(player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.BLOCKS, 8.0f, 1.0f);
        player.sendTitle(Color.GREEN + "Waystone Created", Color.TEAL + waystone.decodeName(data), 1, 3, 1);
    }

    private boolean isLodestone(BlockEvent event) {
        return event.getBlock().getBlockData().getMaterial() == Material.LODESTONE;
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
