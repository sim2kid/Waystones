package com.simmgames.waystones.data;


import com.simmgames.waystones.Accessibility;
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
    public String worldName;
    public Vector3 location;
    public Accessibility access;
    public String name;

    public Location getLocation(Server server)
    {
        return new Location(server.getWorld(worldName), location.X, location.Y, location.Z);
    }

    public Waystone(@NotNull String OwnerUUID, String WorldName, Vector3 BlockLocation, @NotNull String WaystoneName, Accessibility WaystoneAccessibility)
    {
        owner = OwnerUUID;
        worldName = WorldName;
        location = BlockLocation;
        name = WaystoneName;
        if(name.trim() == "")
        {
            name = "(" + BlockLocation.X + ", " + BlockLocation.Y + ", " + BlockLocation.Z + ")";
        }
        access = WaystoneAccessibility;
        if(access == null)
        {
            access = Accessibility.Discoverable;
        }
    }

}
