package com.simmgames.waystones.structure;

import com.simmgames.waystones.util.Default;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class BlockLocation
{
    public Vector3 Position;
    public String WorldUUID;
    public String WorldName;

    public BlockLocation()
    {
        this(Default.UUIDZero, Vector3.Zero);
    }

    public BlockLocation(Location location)
    {
        this(location.getWorld().getUID().toString(), new Vector3(location), location.getWorld().getName());
    }

    public BlockLocation(Location location, boolean useExact)
    {
        this(location.getWorld().getUID().toString(), new Vector3(location, useExact), location.getWorld().getName());
    }

    public BlockLocation(@NotNull String worldUUID, @NotNull Vector3 position, @NotNull String worldName)
    {
        Position = position;
        WorldUUID = worldUUID;
        WorldName = worldName;
        if(WorldUUID.trim() == "")
        {
           WorldUUID = Default.UUIDZero;
        }
    }

    public BlockLocation(@NotNull String worldUUID, @NotNull Vector3 position)
    {
        this(worldUUID, position, "Unknown");
    }

    public BlockLocation(@NotNull String worldUUID, @NotNull float xPos, @NotNull float yPos, @NotNull float zPos)
    {
        this(worldUUID, new Vector3(xPos, yPos, zPos));
    }

    public double getX()
    {
        return Position.X;
    }
    public double getY()
    {
        return Position.Y;
    }
    public double getZ()
    {
        return Position.Z;
    }

    public Location getLocation(Server server)
    {
        return new Location(server.getWorld(UUID.fromString(this.WorldUUID)), this.getX(), this.getY(), this.getZ());
    }

    public double getDistance(BlockLocation other)
    {
        if(!this.WorldUUID.equalsIgnoreCase(other.WorldUUID))
            return -1;
        return this.Position.getDistance(other.Position);
    }

    @Override
    public String toString()
    {
        return WorldName + " " + Position.toString();
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
