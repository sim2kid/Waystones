package com.simmgames.waystones.data;

import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.structure.Vector3;
import com.simmgames.waystones.util.Default;
import org.bukkit.Location;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WayPlayer
{
    public String UUID;
    public String lastUsername;
    public List<Waystone> KnownWaystones;
    public List<Long> OldTeleports;
    public transient Waystone LastVisited;
    public transient boolean InWaystoneUse;
    public transient boolean InWaystoneDiscover;
    public transient List<Waystone> LastNear = new ArrayList<Waystone>();;
    public transient boolean InWaystoneNearby;

    public WayPlayer()
    {
        this(java.util.UUID.fromString(Default.UUIDZero), "Unknown");
    }
    public WayPlayer(@NotNull UUID playerUUID, @NotNull String playerName)
    {
        this(playerUUID.toString(), playerName);
    }

    public WayPlayer(@NotNull String playerUUID, @NotNull String playerName)
    {
        UUID = playerUUID;
        lastUsername = playerName;
        KnownWaystones = new ArrayList<Waystone>();
        OldTeleports = new ArrayList<>();
    }

    public Waystone Closest()
    {
        if(LastNear == null)
            LastNear = new ArrayList<>();
        if (LastNear.size() == 0)
            return null;
        Waystone closest = LastNear.get(0);
        double dist = Double.MAX_VALUE;
        for(int i = 0; i < LastNear.size(); i++)
        {
            double distance = closest.location.getDistance(LastNear.get(i).location);
            if(dist > distance)
            {
                closest = LastNear.get(i);
                dist = distance;
            }
        }
        return closest;
    }
}
