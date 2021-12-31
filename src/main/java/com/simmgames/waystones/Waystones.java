package com.simmgames.waystones;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Waystones extends JavaPlugin {

    private Logger out;
    private Data data;

    @Override
    public void onEnable() {
        // Plugin startup logic
        out = this.getLogger();
        out.log(Level.INFO, "Waystones is now starting up...");
        getCommand("Waystone").setExecutor(new WaystoneCommand(out));

        data = new Data(out);

        data.waystoneList.AllWaystones.add(new Waystone("Owner's UUID Goes here",
                new Location(getServer().getWorld(".TestWorldOne"), 2.0, 1.0, 4.0), "Jessie", Accessibility.Public));
        data.waystoneList.AllWaystones.add(new Waystone("Owner's GUID Doesn't Go here",
                new Location(getServer().getWorld(".TestWorldOne"), -200.0, 16.0, 8.0), "Fuck", Accessibility.Discoverable));

        data.Save();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        out.log(Level.INFO, "Waystones is now shutting down...");
    }
}
