package com.simmgames.waystones;

import jdk.jfr.internal.LogLevel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Waystones extends JavaPlugin {

    private Logger out;

    @Override
    public void onEnable() {
        // Plugin startup logic
        out = this.getLogger();
        out.log(Level.INFO, "Waystones is now starting up...");
        getCommand("Waystone").setExecutor(new WaystoneCommand(out));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        out.log(Level.INFO, "Waystones is now shutting down...");
    }
}
