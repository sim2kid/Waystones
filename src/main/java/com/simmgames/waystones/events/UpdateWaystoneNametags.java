package com.simmgames.waystones.events;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.util.Work;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

public class UpdateWaystoneNametags extends BukkitRunnable {
    Data data;
    Server server;

    int runTimes;

    public UpdateWaystoneNametags(JavaPlugin plugin, Data data)
    {
        runTimes = 0;
        this.data = data;
        server = plugin.getServer();
    }

    @Override
    public void run() {
        runTimes++;
        runTimes %= 24;
        for(WayPlayer p: data.players)
        {
            Waystone wei = p.LastNear;
            if(wei != null) {
                if(wei.hasNametag)
                    if (p.InWaystoneNearby)
                        if(!Work.UpdateHologram(wei.getLocation(server), UUID.fromString(wei.hologramUUID),
                                wei.decodeName(data)))
                            wei.hologramUUID = Work.CreateHologram(wei.getLocation(server), wei.decodeName(data)).toString();
                if(runTimes == 0) {
                    Location loc = wei.location.getLocation(server);
                    loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 6f, 0.6f);
                }
            }
        }
    }
}
