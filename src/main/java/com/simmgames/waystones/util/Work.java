package com.simmgames.waystones.util;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;

import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Work {
    public static Location FindBlockType(int searchDistance, Location playerLocation, Material type)
    {
        int cx = playerLocation.getBlockX();
        int cy = playerLocation.getBlockY();
        int cz = playerLocation.getBlockZ();

        for(int x = cx - searchDistance; x <= cx + searchDistance; x++)
            for(int y = cy - searchDistance; y <= cy + searchDistance; y++)
                for(int z = cz - searchDistance; z <= cz + searchDistance; z++)
                {
                    double dist = (new Vector3(x,y,z)).getDistance(new Vector3(cx,cy,cz));
                    if(dist < searchDistance * searchDistance)
                    {
                        Location l = new Location(playerLocation.getWorld(), x, y+2, z);
                        if(l.getBlock().getType() == type)
                            return l;
                    }
                }
        return null;
    }
    public static UUID CreateHologram(Location location, String text)
    {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location.add(new Vector(0.5, -1, 0.5)), EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(text);

        hologram.setGravity(false);
        hologram.setInvulnerable(true);
        hologram.setCanMove(false);

        return hologram.getUniqueId();
    }

    public static boolean UpdateHologram(Location location, UUID uuid, String text)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return false;
        }
        armorStand.setCustomName(text);
        return true;
    }

    public static boolean HologramVisibility(Location location, UUID uuid, boolean visibility)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return false;
        }
        armorStand.setCustomNameVisible(visibility);
        return true;
    }

    public static void DestroyHologram(Location location, UUID uuid)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return;
        }
        armorStand.remove();
    }
}
