package com.simmgames.waystones;

import com.simmgames.waystones.commands.DebugCommand;
import com.simmgames.waystones.commands.WaystoneCommand;
import com.simmgames.waystones.commands.WaystoneTabComplete;
import com.simmgames.waystones.data.Config;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.TeleportEffects;
import com.simmgames.waystones.events.UpdateWaystoneNametags;
import com.simmgames.waystones.events.WarpItemEvents;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.items.WarpCrystal;
import com.simmgames.waystones.items.WarpItem;
import com.simmgames.waystones.items.WarpScroll;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.structure.Vector3;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.N;
import sun.awt.ConstrainableGraphics;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Waystones extends JavaPlugin {

    private Logger out;
    private Data data;
    private TeleportEffects effects;
    private NamespacedKey itemTypeKey;
    private BukkitTask task;

    @Override
    public void onEnable() {
        // Plugin startup logic
        out = this.getLogger();
        out.log(Level.INFO, "Waystones is now starting up...");


        out.log(Level.INFO, "Loading all waystones.");
        data = new Data(out, this);
        data.Load();

        out.log(Level.INFO, data.AllWaystones.size() + " waystones have been loaded!");


        saveResource("LICENSE.md", true);
        File f = new File(getDataFolder(), File.separator + "config.yml");
        if(!f.exists()) {
            saveResource("config.yml", false);
            out.log(Level.INFO, "Creating default configuration");
        }else{
            out.log(Level.INFO, "Configuration found, processing load");
        }

        Perm.Setup(this.getServer());
        Config.Setup(this, data);

        out.log(Level.INFO, "Adding Event Listeners");
        effects = new TeleportEffects(this, data);
        WaystoneBlockEvents events = new WaystoneBlockEvents(out, data, this.getServer(), effects);
        getServer().getPluginManager().registerEvents(events, this);


        out.log(Level.INFO, "Registering Commands");
        getCommand("Waystone").setExecutor(new WaystoneCommand(out, data, events, this));
        getCommand("Waystone").setTabCompleter(new WaystoneTabComplete(out, data, events, this));

        getCommand("Webug").setExecutor(new DebugCommand(out, data, events, effects,this));


        if(Config.CustomItems())
        {
            WarpItem.Setup(this);

            WarpItemEvents warpItemEvents = new WarpItemEvents(out, data, this.getServer(), events);
            getServer().getPluginManager().registerEvents(warpItemEvents, this);

            if(Config.UseWarpScroll())
            {
                WarpScroll.Setup(this);
                WarpScroll.AddRecipe();
                out.log(Level.INFO, "Enabled Warp Scrolls");
            }
            if(Config.UseWarpCrystal())
            {
                WarpCrystal.Setup(this);
                WarpCrystal.AddRecipe();
                out.log(Level.INFO, "Enabled Warp Crystals");
            }
        }

        out.log(Level.INFO, "Waystones is now setup!");

        task = new UpdateWaystoneNametags(this, data).runTaskTimer(this, 10, 10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        out.log(Level.INFO, "Waystones is now shutting down...");
        if(task != null)
            task.cancel();
        out.log(Level.INFO, "Stopping tasks");
        if(data != null)
            data.Save();
        else
            out.log(Level.SEVERE, "Could not save Waystone data!!");
        out.log(Level.INFO, "All waystone data has been saved. Goodbye!");
    }
}
