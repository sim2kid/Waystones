package com.simmgames.waystones;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WayPlayer
{
    public UUID UUID;
    public List<Location> KnownWaystones;

    public WayPlayer(@NotNull UUID playerUUID)
    {
        UUID = playerUUID;
        KnownWaystones = new ArrayList<Location>();
    }
}
