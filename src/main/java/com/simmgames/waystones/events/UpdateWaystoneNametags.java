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

import java.util.Random;
import java.util.UUID;

public class UpdateWaystoneNametags extends BukkitRunnable {
    Data data;
    Server server;
    JavaPlugin plugin;
    Random ran;
    int runTimes;

    public UpdateWaystoneNametags(JavaPlugin plugin, Data data)
    {
        runTimes = 0;
        this.data = data;
        server = plugin.getServer();
        ran = new Random();
        this.plugin = plugin;
    }

    @Override
    public void run() {
        runTimes++;
        runTimes %= 20;
        if(data.players == null)
            return;
        for(WayPlayer p: data.players)
        {
            if(p == null)
                return;
            Waystone wei = p.LastNear;
            if(wei != null) {
                if(wei.hasNametag) {
                    if (p.InWaystoneNearby)
                        if (!Work.UpdateHologram(wei.getLocation(server), UUID.fromString(wei.hologramUUID),
                                wei.decodeName(data)))
                            wei.hologramUUID = Work.CreateHologram(wei.getLocation(server), wei.decodeName(data)).toString();
                } else {
                    if(p.InWaystoneNearby)
                        Work.DestroyUnmarkedHolograms(wei.getLocation(server));
                }
                if(runTimes == 0 || runTimes == 10) {
                    Location loc = wei.location.getLocation(server);
                    if(wei.timeLeftUntilFunctional() > 0)
                    {
                        if(wei.timeLeftUntilFunctional() > 120)
                        {
                            loc.getWorld().playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, SoundCategory.BLOCKS, 0.3f, 0f);
                        } else {
                            float remap = (1-(wei.timeLeftUntilFunctional() / 120f)) * 0.6f;
                            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 6f, remap);
                        }
                    }
                    else if(runTimes == 0)
                    {
                        if(wei.RunReactivateEvent())
                            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 5f, 0.5f);
                        loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 6f, 0.6f);
                    }
                }

                if(runTimes == 3 || runTimes == 8 || runTimes == 13 || runTimes == 18) {
                    if (wei.timeLeftUntilFunctional() > 0) {
                        if(ran.nextFloat() < 0.1) {
                            if (wei.timeLeftUntilFunctional() > 120) {
                                Location loc = wei.location.getLocation(server);
                                loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 0.3f);
                                new PlaySoundAfterTime(loc.getWorld(), loc, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 0.3f).runTaskLater(plugin, (long) (2 * 20));
                                for(int i = 1; i < 5; i++) {
                                    if (ran.nextFloat() < 0.2)
                                        if(ran.nextBoolean())
                                            new PlaySoundAfterTime(loc.getWorld(), loc, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 0.3f).runTaskLater(plugin, (long) (i * 2.3 * 20) + 2);
                                        else
                                            new PlaySoundAfterTime(loc.getWorld(), loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 0.3f).runTaskLater(plugin, (long) (i * 2.3 * 20) + 2);

                                }
                            }
                        }
                        if(ran.nextFloat() < 0.01)
                        {
                            Location loc = wei.location.getLocation(server);
                            for(int i = 1; i < 7; i++) {
                                if(ran.nextBoolean())
                                    new PlaySoundAfterTime(loc.getWorld(), loc, Sound.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 0.2f, 0f).runTaskLater(plugin, (long) (i * 20 * 0.5));
                            }
                        }
                    }
                }
            }
        }
    }
}
