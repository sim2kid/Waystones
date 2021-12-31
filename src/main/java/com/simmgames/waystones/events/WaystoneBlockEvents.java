package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class WaystoneBlockEvents implements Listener
{
    Logger out;
    Data data;

    public WaystoneBlockEvents(Logger logger, Data pluginData)
    {
        out = logger;
        data = pluginData;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(!isLodestone(event))
            return;
        // Ask to create Waystone
        // Wait for command on creation.
    }

    @EventHandler
    public void onBlockTouch(BlockDamageEvent event)
    {
        if(!isLodestone(event))
            return;
        // check if it is a waystone
        // check if player owns waystone
        // IF player owns waystone, let them know they are about to destroy the waystone
        // ELSE let the player know who owns the waystone and what it's name is
        // Plus tell the player if they discovered the waystone

        // If not a waystone, ask player if they want to create a waystone
        // then wait for the creation command
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(!isLodestone(event))
            return;
        // Check if it is a waystone
        // If it is a waystone, check if it's owned by the player initiating the command
        // If it is, notify player that they destoryed the waystone
        // Update waystone data
        // Update all online player's data
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        // Check for waystones near the player
        // Update whether the player is in a waystone or not
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // Load the player's data into the program

        // Update player's known Waystones list.
        // If waystones are missing, let player know that they have been destroyed.
        // If player's owned waystones are missing, let them know that they have been destroyed

        // Check for waystones near the player
        // Update whether the player is in a waystone or not
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        // Retire the player's data from the program
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        // Check if player is near a waystone or not.
    }



    private Waystone nearbyWaystone(Location sourceLoc)
    {

        return null;
    }

    private boolean isLodestone(BlockEvent event) {
        return event.getBlock().getBlockData().getMaterial() == Material.LODESTONE;
    }
}
