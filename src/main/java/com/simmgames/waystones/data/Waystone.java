package com.simmgames.waystones.data;


import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class Waystone {
    public String owner;
    public BlockLocation location;
    public Accessibility access;
    public String name;

    public Location getLocation(Server server)
    {
        return new Location(server.getWorld(location.WorldUUID), location.getX(), location.getY(), location.getZ());
    }

    public Waystone(@NotNull String OwnerUUID, BlockLocation BlockLocation, @NotNull String WaystoneName, Accessibility WaystoneAccessibility)
    {
        this(OwnerUUID, BlockLocation.WorldUUID, BlockLocation.Position, WaystoneName, WaystoneAccessibility);
    }

    public Waystone(@NotNull String OwnerUUID, String WorldUUID, Vector3 BlockLocation, @NotNull String WaystoneName, Accessibility WaystoneAccessibility)
    {
        owner = OwnerUUID;
        location = new BlockLocation(WorldUUID, BlockLocation);
        name = WaystoneName;
        if(name.trim() == "")
        {
            name = location.toString();
        }
        access = WaystoneAccessibility;
        if(access == null)
        {
            access = Accessibility.Discoverable;
        }
    }

}
