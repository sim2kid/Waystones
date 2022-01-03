package com.simmgames.waystones.commands;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class DebugCommand implements CommandExecutor {
    Logger out;
    Data data;
    WaystoneBlockEvents events;
    Server server;

    public DebugCommand(Logger pluginLogger, Data data, WaystoneBlockEvents events, JavaPlugin plugin)
    {
        out = pluginLogger;
        this.data = data;
        this.events = events;
        server = plugin.getServer();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player))
            return true;
        Player p = (Player) sender;

        return true;
    }
}
