package com.simmgames.waystones.structure;

import com.simmgames.waystones.util.Default;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlockLocation
{
    public Vector3 Position;
    public String WorldUUID;

    public BlockLocation()
    {
        this(Default.UUIDZero, Vector3.Zero);
    }

    public BlockLocation(Location location)
    {
        this(location.getWorld().getUID().toString(), new Vector3(location));
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

    public BlockLocation(@NotNull String worldUUID, @NotNull float xPos, @NotNull float yPos, @NotNull float zPos)
    {
        this(worldUUID, new Vector3(xPos, yPos, zPos));
    }

    public float getX()
    {
        return Position.X;
    }
    public float getY()
    {
        return Position.Y;
    }
    public float getZ()
    {
        return Position.Z;
    }

    public Location getLocation(Server server)
    {
        return new Location(server.getWorld(this.WorldUUID), this.getX(), this.getY(), this.getZ());
    }

    public double getDistance(BlockLocation other)
    {
        if(this.WorldUUID != other.WorldUUID)
            return -1;
        return this.Position.getDistance(other.Position);
    }

    public String Formatted(Server server)
    {
        World w = server.getWorld(this.WorldUUID);
        return w.getName() + " " + Position.toString();
    }

    @Override
    public String toString()
    {
        return "{[World: " + WorldUUID + "]," + Position.toString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockLocation that = (BlockLocation) o;
        return Position.equals(that.Position) && WorldUUID.equals(that.WorldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Position, WorldUUID);
    }
}
