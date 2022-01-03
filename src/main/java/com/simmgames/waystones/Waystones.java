package com.simmgames.waystones;

import com.simmgames.waystones.commands.DebugCommand;
import com.simmgames.waystones.commands.WaystoneCommand;
import com.simmgames.waystones.commands.WaystoneTabComplete;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.UpdateWaystoneNametags;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.structure.Vector3;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
        out.log(Level.INFO, "Loading all waystones.");
        data = new Data(out, this);
        data.Load();
        out.log(Level.INFO, data.AllWaystones.size() + " waystones have been loaded!");

        Perm.Setup(this.getServer());

        out.log(Level.INFO, "Adding Event Listeners");
        WaystoneBlockEvents events = new WaystoneBlockEvents(out, data, this.getServer());
        getServer().getPluginManager().registerEvents(events, this);

        out.log(Level.INFO, "Registering Commands");
        getCommand("Waystone").setExecutor(new WaystoneCommand(out, data, events, this));
        getCommand("Waystone").setTabCompleter(new WaystoneTabComplete(out, data, events, this));

        getCommand("Webug").setExecutor(new DebugCommand(out, data, events, this));

        out.log(Level.INFO, "Waystones is now setup!");

        BukkitTask task = new UpdateWaystoneNametags(this, data).runTaskTimer(this, 10, 10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        out.log(Level.INFO, "Waystones is now shutting down...");
        data.Save();
        out.log(Level.INFO, "All waystone data has been saved. Goodbye!");
    }
}
