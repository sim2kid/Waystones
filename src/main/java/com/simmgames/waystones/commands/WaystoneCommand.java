package com.simmgames.waystones.commands;

import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.structure.BlockLocation;
import com.simmgames.waystones.util.Default;
import com.simmgames.waystones.util.Work;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaystoneCommand implements CommandExecutor {
    Logger out;
    Data data;
    WaystoneBlockEvents events;
    Server server;

    public WaystoneCommand(Logger pluginLogger, Data data, WaystoneBlockEvents events, JavaPlugin plugin)
    {
        out = pluginLogger;
        this.data = data;
        this.events = events;
        server = plugin.getServer();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("help")) {
                help(sender, args);
                return true;
            } else if(args[0].equalsIgnoreCase("create"))
            {
                create(sender, args);
                return true;
            } else if(args[0].equalsIgnoreCase("nametag"))
            {
                nametag(sender, args);
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
            return;
        }

        // check if waystone already exists there
        if(events.GetWaystoneAt(lode) != null)
        {
            p.sendMessage("Lodestone is already a Waystone. Can't build a new one here.");
            return;
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
                    return;
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
        newWaystone.hologramUUID = Work.CreateHologram(lode.getBlock().getLocation(), newWaystone.decodeName(data)).toString();
        data.AllWaystones.add(newWaystone);
        events.OnCreateWaystone(p, newWaystone);
    }

    void nametag(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, Color.RED + "You must be a player to modify a Waystone.");
            return;
        }
        Player p = (Player) sender;

        // Check for create perms

        // Check if there is a lodestone nearby
        Location lode = Work.FindBlockType(data.LodestoneSearchRadius(), p.getLocation(), Material.LODESTONE);
        if(lode == null)
        {
            p.sendMessage("No Waystone nearby. Make sure you are within " + data.LodestoneSearchRadius() + " blocks of one.");
            return;
        }

        // check if waystone already exists there
        Waystone wei = events.GetWaystoneAt(lode);
        if(wei == null)
        {
            p.sendMessage("Lodestone is not currently a Waystone. Cannot toggle it's nametag.");
            return;
        }

        // check if own waystone
        if(!wei.owner.equalsIgnoreCase(p.getUniqueId().toString()))
        {
            p.sendMessage("You must be the owner of this Waystone to modify it's properties.");
            return;
        }


        if(args.length < 2)
        {
            // Cannot Toggle
            p.sendMessage("State cannot be blank for nametag. Please use\n/waystone nametag <true|false>");
            return;
        } else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("t") || args[1].equalsIgnoreCase("on"))
        {
            // turn on
            p.sendMessage("Turning on the NameTag for the nearest waystone.");
            Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), true);
        } else if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("f") || args[1].equalsIgnoreCase("off"))
        {
            // turn off
            p.sendMessage("Turning off the NameTag for the nearest waystone.");
            Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), false);
        } else
        {
            // Unknown
            p.sendMessage("'" + args[1] + "' is not a valid state for the nametags. Please use\n/waystone nametag <true|false>");
            return;
        }
    }
}
