package com.simmgames.waystones;

import com.simmgames.waystones.commands.WaystoneCommand;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
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

        data = new Data(out, this);

        data.waystoneList.AllWaystones.add(new Waystone("Owner's UUID Goes here", ".TestWorldOne",
                new Vector3(2, 4, 1), "Jessie", Accessibility.Public));
        data.waystoneList.AllWaystones.add(new Waystone("Owner's GUID Doesn't Go here",".TestWorldOne",
                new Vector3(232, -64, 11), "Fuck", Accessibility.Discoverable));

        data.Save();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        out.log(Level.INFO, "Waystones is now shutting down...");
    }
}
