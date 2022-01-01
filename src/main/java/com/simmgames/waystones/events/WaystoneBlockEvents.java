package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
    public void onPlayerInteract(EntityInteractEvent event)
    {
        if(!(event.getEntity() instanceof Player))
            return;
        Player p = (Player)event.getEntity();
        if(p == null)
            return;
        if(!(event.getBlock().getBlockData().getMaterial() == Material.LODESTONE))
            return;

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
        // If it is, notify player that they destoryed the waystone
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
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();
        p.sendMessage("You have joined the server");

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
        Player p = event.getPlayer();
        out.log(Level.INFO, p.name().toString() + " has left the server, imo");
        // Retire the player's data from the program
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        //p.sendMessage("You have moved.");
        // Check if player is near a waystone or not.
    }

    private void OnExploded(Block b)
    {
        out.log(Level.INFO, "Lodestone has Exploded!");
    }

    private void DestroyWaystone(Location location)
    {

    }


    private Waystone nearbyWaystone(Location sourceLoc)
    {

        return null;
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
