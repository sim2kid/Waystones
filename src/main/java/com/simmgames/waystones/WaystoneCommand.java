package com.simmgames.waystones;

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
}
