package com.simmgames.waystones.data;


import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.structure.Vector3;
import com.simmgames.waystones.util.Default;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

public class Waystone {
    public String owner;
    public BlockLocation location;
    public Accessibility access;
    public String name;
    public String hologramUUID;
    public long TimeWhenFunctional;
    public boolean hasNametag;

    public Location getLocation(Server server)
    {
        return location.getLocation(server);
    }

    public Waystone()
    {
        owner = Default.UUIDZero;
        location = new BlockLocation();
        access = Accessibility.Discoverable;
        name = "Unknown";
        hologramUUID = Default.UUIDZero;
        TimeWhenFunctional = 0;
        hasNametag = false;
    }

    public Waystone(@NotNull String OwnerUUID, BlockLocation BlockLocation, @NotNull String WaystoneName,
                    Accessibility WaystoneAccessibility, int windupTime, boolean hasNametag)
    {
        owner = OwnerUUID;
        location = BlockLocation;
        name = WaystoneName;
        TimeWhenFunctional = currentTimeMillis() + (windupTime * 1000);
        this.hasNametag = hasNametag;
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

    public boolean canUse()
    {
        return TimeWhenFunctional <= currentTimeMillis();
    }
    public int timeLeftUntilFunctional()
    {
        return (int) Math.max((TimeWhenFunctional - currentTimeMillis())/1000, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waystone waystone = (Waystone) o;
        return location.equals(waystone.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    public String decodeName(Data data)
    {
        String accessor = "";
        if(this.access == Accessibility.Public)
            accessor = " - Public";
        if(this.access == Accessibility.Private)
            accessor = " - Private";
        if(!canUse())
            accessor = " T-" + timeLeftUntilFunctional() + "s";

        String maker = "";
        if(!owner.equalsIgnoreCase(Default.UUIDZero)) {
            maker = "[" + data.GrabPlayer(owner).lastUsername + "] ";
        } else if (!owner.equalsIgnoreCase(Default.UUIDOne))
        {
            maker = "[Admin] ";
        }
        return  maker + name + accessor;
    }
}
