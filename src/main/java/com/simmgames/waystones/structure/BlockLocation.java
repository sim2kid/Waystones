package com.simmgames.waystones.structure;

import com.simmgames.waystones.util.Default;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class BlockLocation
{
    public Vector3 Position;
    public String WorldUUID;

    public BlockLocation()
    {
        this(Default.UUIDZero, Vector3.Zero);
    }

    public BlockLocation(@NotNull String worldUUID, @NotNull Vector3 position)
    {
        Position = position;
        WorldUUID = worldUUID;
        if(WorldUUID.trim() == "")
        {
           WorldUUID = Default.UUIDZero;
        }
    }

    public BlockLocation(@NotNull String worldUUID, @NotNull int xPos, @NotNull int yPos, @NotNull int zPos)
    {
        this(worldUUID, new Vector3(xPos, yPos, zPos));
    }

    public int getX()
    {
        return Position.X;
    }
    public int getY()
    {
        return Position.Y;
    }
    public int getZ()
    {
        return Position.Z;
    }

    public Location getLocation(Server server)
    {
        return new Location(server.getWorld(this.WorldUUID), this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString()
    {
        return "{[World: " + WorldUUID + "]," + Position.toString() + '}';
    }
}
