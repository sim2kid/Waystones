package com.simmgames.waystones.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaystoneCommand implements CommandExecutor {
    Logger out;

    public WaystoneCommand(Logger pluginLogger)
    {
        out = pluginLogger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("help")) {
                help(sender, args);
                return true;
            }

            if(sender instanceof Player)
            {
                Player p = (Player) sender;
            } else {
                out.log(Level.INFO, "You must be a player to teleport using Waystones. Run '/Waystone help' for more info.");
            }
        } else {
            help(sender, args);
        }
        return true;
    }

    void help(CommandSender sender, String[] args)
    {
        String message = "This is the help message :P. Args:";
        for (String s:
             args) {
            message += ", " + s;
        }

        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            p.sendMessage(message);
        } else {
            out.log(Level.INFO, message);
        }
    }

    void create(CommandSender sender, String[] args)
    {
        String message = "";
        if(args.length < 2)
        {
            message = "You need to include a name for your waystone.\n/waystone create <name>";
        }
        if(args[1].trim() == "")
            message = "Your waystone name can not be blank.\n/waystone create <name>";

        String waystoneName = args[1];
        // Check if the waystone name already exists

        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            p.sendMessage(message);
        } else {
            out.log(Level.INFO, message);
        }
    }
}
