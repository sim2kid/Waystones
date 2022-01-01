package com.simmgames.waystones.util;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;

import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
}
