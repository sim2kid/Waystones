package com.simmgames.waystones;

import com.simmgames.waystones.commands.*;
import com.simmgames.waystones.data.Config;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.events.TeleportEffects;
import com.simmgames.waystones.tasks.UpdateWaystoneNametags;
import com.simmgames.waystones.events.WarpItemEvents;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.items.WarpCrystal;
import com.simmgames.waystones.items.WarpItem;
import com.simmgames.waystones.items.WarpScroll;
import com.simmgames.waystones.permissions.Perm;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Waystones extends JavaPlugin {

    private Logger out;
    private Data data;
    private TeleportEffects effects;
    private NamespacedKey itemTypeKey;
    private BukkitTask task;
    public Plugin NbtApi;
    public boolean SupportsNBT = false;

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

        getCommand("Warp").setExecutor(new TeleportCommand(out, data, events, this));
        getCommand("Warp").setTabCompleter(new TeleportTabComplete(out, data, events, this));

        getCommand("Webug").setExecutor(new DebugCommand(out, data, events, effects,this));

        NbtApi = getServer().getPluginManager().getPlugin("NBTApi");
        if(NbtApi == null)
        {
            out.log(Level.WARNING, "Missing NBTApi. Without it, items will not spawn with correct texture data.");
        }
        else
        {
            out.log(Level.INFO, "NBTApi Detected. Items will spawn with vanilla texture data. Requires Resource Pack to function.");
            SupportsNBT = true;
        }

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
