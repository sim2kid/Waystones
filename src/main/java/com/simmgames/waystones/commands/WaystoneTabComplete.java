package com.simmgames.waystones.commands;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.permissions.Perm;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaystoneTabComplete implements TabCompleter
{
    Logger out;
    Data data;
    WaystoneBlockEvents events;
    Server server;

    public WaystoneTabComplete(Logger pluginLogger, Data data, WaystoneBlockEvents events, JavaPlugin plugin)
    {
        out = pluginLogger;
        this.data = data;
        this.events = events;
        server = plugin.getServer();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> toReturn = new ArrayList<String>();
        if(!sender.hasPermission(Perm.Waystone))
            return toReturn;

        if(args.length == 1)
        {
            toReturn.add("help");
            if(sender.hasPermission(Perm.Create))
                toReturn.add("create");
            if(sender.hasPermission(Perm.Nametag))
                toReturn.add("nametag");
        }
        if(args.length > 1)
        {
            if(args[0].equalsIgnoreCase("create") && sender.hasPermission(Perm.Create))
            {
                if(args.length == 3)
                {
                    if(sender.hasPermission(Perm.CreateDiscoverable))
                        toReturn.add("default");
                    if(sender.hasPermission(Perm.CreatePrivate))
                        toReturn.add("private");
                    if(sender.hasPermission(Perm.CreatePublic))
                        toReturn.add("public");
                }
                if(args.length == 4)
                {
                    if(sender.hasPermission(Perm.CreateAdmin))
                        toReturn.add("admin");
                }
            }
            else if(args[0].equalsIgnoreCase("nametag") && sender.hasPermission(Perm.Nametag))
            {
                if(args.length == 2)
                {
                    toReturn.add("true");
                    toReturn.add("false");
                }
            }
        }
        return toReturn;
    }
}
