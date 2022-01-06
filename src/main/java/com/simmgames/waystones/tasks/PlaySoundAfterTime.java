package com.simmgames.waystones.tasks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaySoundAfterTime extends BukkitRunnable {

    World world;
    Sound sound;
    SoundCategory soundCategory;
    Location location;
    float volume;
    float pitch;

    public  PlaySoundAfterTime(World world, Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        this.world = world;
        this.sound = sound;
        this.location = location;
        this.soundCategory = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run() {
        world.playSound(location,sound,soundCategory,volume,pitch);
    }
}
