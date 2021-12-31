package com.simmgames.waystones.structure;

import org.bukkit.Location;

public class Vector3 {
    public int X;
    public int Y;
    public int Z;

    public static Vector3 Zero = new Vector3(0,0,0);

    public Vector3(int xPos, int yPos, int zPos)
    {
        X = xPos;
        Y = yPos;
        Z = zPos;
    }

    public Vector3(Location location)
    {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
