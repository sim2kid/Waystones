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
    public transient Waystone LastVisited;
    public transient boolean InWaystoneUse;
    public transient boolean InWaystoneDiscover;
    public transient Waystone LastNear;
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
    }
}
