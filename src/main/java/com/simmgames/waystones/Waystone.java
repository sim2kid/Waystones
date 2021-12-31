package com.simmgames.waystones;


import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Waystone {
    public String owner;
    public Location location;
    public Accessibility access;
    public String name;

    public Waystone(@NotNull String OwnerUUID, @NotNull Location BlockLocation, @NotNull String WaystoneName, Accessibility WaystoneAccessibility)
    {
        owner = OwnerUUID;
        location = BlockLocation;
        name = WaystoneName;
        if(name.isBlank())
        {
            name = "(" + BlockLocation.getBlockX() + ", " + BlockLocation.getBlockY() + ", " + BlockLocation.getBlockY() + ")";
        }
        access = WaystoneAccessibility;
        if(access == null)
        {
            access = Accessibility.Discoverable;
        }
    }

}
