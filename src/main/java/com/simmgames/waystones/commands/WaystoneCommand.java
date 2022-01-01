package com.simmgames.waystones.commands;

import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.util.Default;
import com.simmgames.waystones.util.Work;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaystoneCommand implements CommandExecutor {
    Logger out;
    Data data;
    WaystoneBlockEvents events;

    public WaystoneCommand(Logger pluginLogger, Data data, WaystoneBlockEvents events)
    {
        out = pluginLogger;
        this.data = data;
        this.events = events;
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
        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, Color.RED + "You must be a player to create a Waystone.");
            return;
        }
        Player p = (Player) sender;

        // Check for create perms

        // Check if there is a lodestone nearby
        Location lode = Work.FindBlockType(data.LodestoneSearchRadius(), p.getLocation(), Material.LODESTONE);
        if(lode == null)
        {
            p.sendMessage("No Lodestone nearby to turn into a Waystone. Make sure you are within " + data.LodestoneSearchRadius() + " blocks.");
        }

        if(args.length < 2)
        {
            p.sendMessage("You need to include a name for your waystone.\n/waystone create <name> [default|public|private]");
            return;
        }
        if(args[1].trim() == "") {
            p.sendMessage("Your waystone name can not be blank.\n/waystone create <name> [default|public|private]");
            return;
        }

        String waystoneName = args[1];
        // Check if the waystone name already exists
        String worldUUID = p.getWorld().getUID().toString();

        for(Waystone wei : data.AllWaystones)
        {
            if(wei.location.WorldUUID.equalsIgnoreCase(worldUUID) &&
                    (wei.owner.equalsIgnoreCase(p.getUniqueId().toString()) ||
                            wei.access == Accessibility.Public))
            {
                if(waystoneName.trim().equalsIgnoreCase(wei.name.trim()))
                {
                    p.sendMessage("'" + waystoneName + "' already exists as one of your waystones or is already a public waystone.");
                }
            }
        }
        Accessibility access = Accessibility.Discoverable;

        if(args.length >= 3)
            if(args[2] != null)
                switch (args[2].trim().toLowerCase())
                {
                    case "public":
                        // Check perms
                        access = Accessibility.Public;
                        break;
                    case "private":
                        // Check perms
                        access = Accessibility.Private;
                        break;
                    case "default":
                    case "discoverable":
                    case "discover":
                        // Check perms
                        access = Accessibility.Discoverable;
                        break;
                    default:
                        p.sendMessage("'" + args[2].trim() + "' is an unknown privacy setting.\n/waystone create <name> [default|public|private]");
                        return;
                }

        // Create Waystone
        Waystone newWaystone = new Waystone(p.getUniqueId().toString(), new BlockLocation(lode), waystoneName.trim(), access);
        data.AllWaystones.add(newWaystone);
        events.OnCreateWaystone(p, newWaystone);
    }
}
