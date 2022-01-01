package com.simmgames.waystones.data;

import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WayPlayer
{
    public UUID UUID;
    public String lastUsername;
    public List<Vector3> KnownWaystones;

    public WayPlayer()
    {
        this(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"), "Unknown");
    }
    public WayPlayer(@NotNull UUID playerUUID, @NotNull String playerName)
    {
        UUID = playerUUID;
        lastUsername = playerName;
        KnownWaystones = new ArrayList<Vector3>();
    }
}
