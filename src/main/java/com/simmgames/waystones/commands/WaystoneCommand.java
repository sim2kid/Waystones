package com.simmgames.waystones.commands;

import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Local;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.permissions.Perm;
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
import java.util.*;
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
        if(!sender.hasPermission(Perm.Waystone))
        {
            sender.sendMessage(Local.NoPermsCommand());
            return true;
        }
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
            } else if(args[0].equalsIgnoreCase("list"))
            {
                list(sender, args);
                return true;
            } else if(args[0].equalsIgnoreCase("tp"))
            {
                tp(sender, args);
                return true;
            } else if(args[0].equalsIgnoreCase("teleport"))
            {
                teleport(sender, args);
                return true;
            }


            if(sender instanceof Player)
            {
                Player p = (Player) sender;
            } else {
                out.log(Level.INFO, ChatColor.RED + "You must be a player to teleport using Waystones. Run '/Waystone help' for more info.");
            }
        } else {
            help(sender, args);
        }
        return true;
    }

    void help(CommandSender sender, String[] args)
    {
        String message = ChatColor.AQUA + "This is the main command to use this plugin.\n";
        if(sender.hasPermission(Perm.Create))
            message += ChatColor.GOLD + "/waystone create <name> [accessibility]" + ChatColor.AQUA +  " Make a new Waystone.";
        if(sender.hasPermission(Perm.List))
            message += ChatColor.GOLD + "/waystone list" + ChatColor.AQUA +  " See all known Waystones.";
        if(sender.hasPermission(Perm.Nametag))
            message += ChatColor.GOLD + "/waystone nametag <toggle>" + ChatColor.AQUA +  " Turns on/off a Waystone's nametag.";
        if(sender.hasPermission(Perm.Teleport)) {
            message += ChatColor.GOLD + "/waystone tp <public/own waystone name>" + ChatColor.AQUA + " Teleport to your waystone or to a public waystone.";
            message += ChatColor.GOLD + "/waystone teleport <creator username> <waystone name>" + ChatColor.AQUA +  " Teleport to any accessible waystone.";
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
        // Check for create perms
        if(!sender.hasPermission(Perm.Create))
        {
            sender.sendMessage(Local.NoPermsCommand());
            return;
        }

        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, ChatColor.RED + "You must be a player to create a Waystone.");
            return;
        }
        Player p = (Player) sender;
        int waystoneCount = Work.FilterToUser(data.AllWaystones, p.getUniqueId().toString()).size();
        if(!(waystoneCount < data.WaystoneCreationLimit(p) || p.hasPermission(Perm.CreateBypass) || p.hasPermission(Perm.CreateAdmin)))
        {
            sender.sendMessage(ChatColor.RED + "You have reached your max number of Waystones that you can create. ["
                    + waystoneCount + "/" + data.WaystoneCreationLimit(p) + "].");
            return;
        }


        // Check if there is a lodestone nearby
        Location lode = Work.FindBlockType(data.LodestoneSearchRadius(), p.getLocation(), Material.LODESTONE);
        if(lode == null)
        {
            p.sendMessage( ChatColor.RED + "No Lodestone nearby to turn into a Waystone. Make sure you are within " +
                    data.LodestoneSearchRadius() + " blocks.");
            return;
        }

        // check if waystone already exists there
        if(events.GetWaystoneAt(lode) != null)
        {
            p.sendMessage(ChatColor.RED + "Lodestone is already a Waystone. Can't build a new one here.");
            return;
        }

        if(args.length < 2)
        {
            p.sendMessage(ChatColor.RED + "You need to include a name for your waystone.\n"
                    + ChatColor.GOLD + "/waystone create <name> [default|public|private]");
            return;
        }
        if(args[1].trim() == "") {
            p.sendMessage(ChatColor.RED + "Your waystone name can not be blank.\n"
                    + ChatColor.GOLD +  "/waystone create <name> [default|public|private]");
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
                    p.sendMessage(ChatColor.RED + "'" + waystoneName +
                            "' already exists as one of your waystones or is already a public waystone.");
                    return;
                }
            }
        }

        boolean admin = false;
        if(args.length >= 4)
            if(args[3] != null)
                if(sender.hasPermission(Perm.CreateAdmin))
                    admin = args[3].equalsIgnoreCase("admin");
                else
                {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to make Admin waystones.");
                    return;
                }
        if(!(waystoneCount < data.WaystoneCreationLimit(p) || p.hasPermission(Perm.CreateBypass)) && !admin )
        {
            sender.sendMessage(ChatColor.RED + "You have reached your max number of Waystones that you can create. ["
                    + waystoneCount + "/" + data.WaystoneCreationLimit(p) + "].");
            return;
        }

        Accessibility access = null;

        String accessStr = data.DefaultAccess();
        if(args.length >= 3)
            if(args[2] != null)
                accessStr = args[2];

        switch (accessStr.trim().toLowerCase())
        {
            case "public":
                if(!sender.hasPermission(Perm.CreatePublic))
                {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to make Public Waystones.");
                    return;
                }

                // Check perms
                access = Accessibility.Public;
                break;
            case "private":
                if(!sender.hasPermission(Perm.CreatePrivate))
                {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to make Private Waystones.");
                    return;
                }
                if(admin)
                {
                    sender.sendMessage(ChatColor.RED + "You can't create a Private Admin Waystone.");
                    return;
                }
                // Check perms
                access = Accessibility.Private;
                break;
            case "default":
            case "discoverable":
            case "discover":
                if(!sender.hasPermission(Perm.CreateDiscoverable))
                {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to make Discoverable Waystones.");
                    return;
                }
                // Check perms
                access = Accessibility.Discoverable;
                break;
            default:
                p.sendMessage(ChatColor.RED + "'" + args[2].trim() + "' is an unknown privacy setting.\n"
                        + ChatColor.GOLD + "/waystone create <name> [default|public|private]");
                return;
        }

        int chargeTime = data.WaystoneChargeTime(p);

        // Create Waystone
        Waystone newWaystone = new Waystone((admin ? Default.UUIDOne : p.getUniqueId().toString()),
                new BlockLocation(lode), waystoneName.trim(), access, chargeTime);
        newWaystone.hologramUUID = Work.CreateHologram(lode.getBlock().getLocation(), newWaystone.decodeName(data),
                data.DefaultNametag()).toString();
        if(newWaystone.access == Accessibility.Discoverable ||
                (newWaystone.access == Accessibility.Private &&
                        newWaystone.owner.equalsIgnoreCase(p.getUniqueId().toString())))
            data.AllWaystones.add(newWaystone);
        events.OnCreateWaystone(p, newWaystone);
    }

    void nametag(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission(Perm.Nametag))
        {
            sender.sendMessage(Local.NoPermsCommand());
            return;
        }


        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, ChatColor.RED + "You must be a player to modify a Waystone.");
            return;
        }
        Player p = (Player) sender;

        // Check for create perms

        // Check if there is a lodestone nearby
        Location lode = Work.FindBlockType(data.LodestoneSearchRadius(), p.getLocation(), Material.LODESTONE);
        if(lode == null)
        {
            p.sendMessage(ChatColor.RED + "No Waystone nearby. Make sure you are within " + data.LodestoneSearchRadius()
                    + " blocks of one.");
            return;
        }

        // check if waystone already exists there
        Waystone wei = events.GetWaystoneAt(lode);
        if(wei == null)
        {
            p.sendMessage(ChatColor.RED + "Lodestone is not currently a Waystone. Cannot toggle it's nametag.");
            return;
        }

        // check if own waystone
        if(!wei.owner.equalsIgnoreCase(p.getUniqueId().toString()) && !sender.hasPermission(Perm.NametagOther))
        {
            p.sendMessage(ChatColor.RED + "You must be the owner of this Waystone to modify it's properties.");
            return;
        }


        if(args.length < 2)
        {
            // Cannot Toggle
            p.sendMessage(ChatColor.RED + "State cannot be blank for nametag. Please use\n" + ChatColor.GOLD
                    + "/waystone nametag <true|false>");
            return;
        } else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("t") ||
                args[1].equalsIgnoreCase("on"))
        {
            // turn on
            p.sendMessage(ChatColor.GREEN + "Turning on the NameTag for the nearest waystone.");
            Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), true);
        } else if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("f") ||
                args[1].equalsIgnoreCase("off"))
        {
            // turn off
            p.sendMessage(ChatColor.YELLOW + "Turning off the NameTag for the nearest waystone.");
            Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), false);
        } else
        {
            // Unknown
            p.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid state for the nametags. Please use\n"
                    + ChatColor.GOLD + "/waystone nametag <true|false>");
            return;
        }
    }
    void list(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission(Perm.List))
        {
            sender.sendMessage(Local.NoPermsCommand());
            return;
        }

        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, ChatColor.RED + "You must be a player to create a Waystone.");
            return;
        }
        Player p = (Player) sender;
        WayPlayer WeiPlayer = data.GrabPlayer(p.getUniqueId().toString());

        String whatToShow = "known";
        if(args.length >= 2)
        {
            whatToShow = args[1];
        }

        List<String> Results = new ArrayList<String>();

        int page = 1;
        if(args.length >= 3)
        {
             try {
                 page = Integer.parseInt(args[2]);
             } catch (Exception e) {
                 page = 1;
            }
        }


        switch (whatToShow.trim().toLowerCase()) {
            case "default":
            case "known":
                for(Waystone wei: data.AllWaystones) {
                    String otherWorld = "";
                    if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                        otherWorld = ChatColor.BLACK + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
                    if(!wei.canUse())
                        otherWorld += ChatColor.DARK_RED + "[Unavailable]";
                    if (wei.access == Accessibility.Public) {
                        String mine = " ";
                        if(wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                            mine = ChatColor.GREEN + "[Mine] ";
                        Results.add(otherWorld + ChatColor.BLUE + "[Public]" + mine + ChatColor.AQUA + wei.name + "\n");
                    }
                    if (wei.access == Accessibility.Private && wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                        Results.add(otherWorld + ChatColor.DARK_PURPLE + "[Private]" + ChatColor.GREEN + "[Mine] "
                                + ChatColor.AQUA + wei.name + "\n");
                }
                for(Waystone wei: WeiPlayer.KnownWaystones) {
                    String otherWorld = "";
                    if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                        otherWorld = ChatColor.BLACK + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
                    if(!wei.canUse())
                        otherWorld += ChatColor.DARK_RED + "[Unavailable]";
                    String mine = ChatColor.DARK_PURPLE + "[" + data.GrabPlayer(wei.owner).lastUsername + "] ";
                    if(wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                        mine = ChatColor.GREEN + "[Mine] ";
                    Results.add(otherWorld + mine + ChatColor.AQUA + wei.name + "\n");
                }
                break;
            case "public":
                for(Waystone wei: data.AllWaystones)
                    if (wei.access == Accessibility.Public) {
                        String otherWorld = "";
                        if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                            otherWorld = ChatColor.BLACK + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
                        if(!wei.canUse())
                            otherWorld += ChatColor.DARK_RED + "[Unavailable]";
                        String mine = " ";
                        if(wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                            mine = ChatColor.GREEN + "[Mine] ";
                        Results.add(otherWorld + ChatColor.BLUE + "[Public]" + mine + ChatColor.AQUA + wei.name + "\n");
                    }
                break;
            case "mine":
                for(Waystone wei: data.AllWaystones)
                    if (wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                    {
                        String otherWorld = "";
                        if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                            otherWorld = ChatColor.BLACK + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
                        if(!wei.canUse())
                            otherWorld += ChatColor.DARK_RED + "[Unavailable]";
                        String access = "";
                        if(wei.access == Accessibility.Private)
                            access = ChatColor.DARK_PURPLE + "[Private]";
                        Results.add(otherWorld + access + ChatColor.GREEN + "[Mine] " + ChatColor.AQUA + wei.name + "\n");
                    }
                break;
            case "unknown":
                if(!sender.hasPermission(Perm.ListUnknown))
                {
                    sender.sendMessage(Local.NoPermsCommand());
                    return;
                }
                for(Waystone wei: data.AllWaystones) {
                    String otherWorld = "";
                    if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                        otherWorld = ChatColor.BLACK + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
                    if(!wei.canUse())
                        otherWorld += ChatColor.DARK_RED + "[Unavailable]";
                    String unknown = ChatColor.GRAY  + "[Unknown] ";
                    if(WeiPlayer.KnownWaystones.contains(wei) || wei.access != Accessibility.Discoverable)
                        continue;
                    String player = ChatColor.GREEN + "[Mine]";
                    if(!wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                        player = ChatColor.DARK_PURPLE + "[" + data.GrabPlayer(wei.owner).lastUsername + "]";

                    Results.add(otherWorld + player + unknown + ChatColor.AQUA + wei.name + "\n");
                }
                break;
            case "all":
                if(!sender.hasPermission(Perm.ListAll))
                {
                    sender.sendMessage(Local.NoPermsCommand());
                    return;
                }
                for(Waystone wei: data.AllWaystones) {
                    String otherWorld = "";
                    if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                        otherWorld = ChatColor.BLACK + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
                    if(!wei.canUse())
                        otherWorld += ChatColor.DARK_RED + "[Unavailable]";
                    if (wei.access == Accessibility.Public){
                        Results.add(otherWorld + ChatColor.BLUE + "[Public] " + ChatColor.AQUA + wei.name + "\n");
                    }
                    else if(wei.access == Accessibility.Private)
                    {
                        if(wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                        {
                            Results.add(otherWorld + ChatColor.GOLD + "[Private] " + ChatColor.AQUA + wei.name + "\n");
                        }
                        else
                        {
                            Results.add(otherWorld + ChatColor.GOLD + "[Private]" + ChatColor.DARK_PURPLE +
                                    "[" + data.GrabPlayer(wei.owner).lastUsername + "] " + ChatColor.AQUA + wei.name + "\n");
                        }
                    }
                    else
                    {
                        String unknown = ChatColor.GRAY  + "[Unknown] ";
                        if(WeiPlayer.KnownWaystones.contains(wei))
                            unknown = " ";
                        String player = ChatColor.GREEN + "[Mine]";
                        if(!wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                            player = ChatColor.DARK_PURPLE + "[" + data.GrabPlayer(wei.owner).lastUsername + "]";
                        Results.add(otherWorld + player + unknown + ChatColor.AQUA + wei.name + "\n");
                    }
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "'" + whatToShow + "' is an unknown thing to list.");
                return;
        }

        int perList = data.DefaultListSize();

        int max = (int) Math.ceil((double) Results.size() / (double) perList);

        page = (int) Math.min(max, Math.max(page, 1));

        Collections.sort(Results);

        String filler = "==========";
        String workingString = filler + " Waystones " + page + "/" + max + " " + filler + "\n";

        for(int i = (page-1)*perList; i < (int)Math.min(page*perList,Results.size()); i++)
            workingString += Results.get(i);

        workingString += ChatColor.RESET +  filler + " Waystones " + page + "/" + max + " "  + filler;

        sender.sendMessage(workingString);
    }

    void tp(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission(Perm.Teleport))
        {
            sender.sendMessage(Local.NoPermsCommand());
            return;
        }

        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, ChatColor.RED + "You must be a player to teleport to a Waystone.");
            return;
        }
        Player p = (Player) sender;
        WayPlayer WeiPlayer = data.GrabPlayer(p.getUniqueId().toString());
        if(!(WeiPlayer.InWaystoneUse || sender.hasPermission(Perm.TeleportIgnoreWaystone)))
        {
            p.sendMessage(ChatColor.RED + "You must be near a Waystone to teleport.");
            return;
        }
        if(!WeiPlayer.LastVisited.canUse() && !sender.hasPermission(Perm.TeleportIgnoreWaystone))
        {
            p.sendMessage(ChatColor.RED + "Origin Waystone is still charging! " + WeiPlayer.LastVisited.timeLeftUntilFunctional()
                    + " seconds left until usable.");
            return;
        }

        List<Waystone> context = Work.GetOwnAndPublicWaystones(p, data);

        if(args.length >= 2)
        {
            String wayName = args[1];
            Waystone way = null;
            for(Waystone wei: context)
                if(wei.name.equalsIgnoreCase(wayName)) {
                    way = wei;
                    break;
                }
            if(way == null)
            {
                sender.sendMessage(ChatColor.RED + "Could not access Waystone '" + wayName + "'.");
                return;
            }
            if(!way.canUse())
            {
                p.sendMessage(ChatColor.RED + "Destination Waystone is still charging. " + way.timeLeftUntilFunctional()
                        + " seconds left until usable.");
                return;
            }

            Waystone origin = null;
            if(WeiPlayer.InWaystoneUse)
            {
                origin = WeiPlayer.LastVisited;
            }
            events.OnTeleport(p, way, origin);
        } else {
            sender.sendMessage(ChatColor.RED + "You must provide a Waystone to teleport to.\n" + ChatColor.GOLD +
                    "/waystone tp <public/own waystone name>");
            return;
        }
    }

    void teleport(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission(Perm.Teleport))
        {
            sender.sendMessage(Local.NoPermsCommand());
            return;
        }

        if(!(sender instanceof Player))
        {
            out.log(Level.INFO, ChatColor.RED + "You must be a player to teleport to a Waystone.");
            return;
        }
        Player p = (Player) sender;
        WayPlayer WeiPlayer = data.GrabPlayer(p.getUniqueId().toString());
        if(!(WeiPlayer.InWaystoneUse || sender.hasPermission(Perm.TeleportIgnoreWaystone)))
        {
            p.sendMessage(ChatColor.RED + "You must be near a Waystone to teleport.");
            return;
        }
        if(!WeiPlayer.LastVisited.canUse() && !sender.hasPermission(Perm.TeleportIgnoreWaystone))
        {
            p.sendMessage(ChatColor.RED + "Origin Waystone is still charging! " + WeiPlayer.LastVisited.timeLeftUntilFunctional()
                    + " seconds left until usable.");
            return;
        }

        List<Waystone> context;
        if(sender.hasPermission(Perm.TeleportUnknown))
            context = Work.GetKnownAndUnknownWaystones(p, data);
        else if(sender.hasPermission(Perm.TeleportAll))
            context = Work.GetKnownWaystones(p, data);
        else
            context = data.AllWaystones;

        if(args.length >= 2)
        {
            String username = args[1];
            if(username.trim().length() == 0)
            {
                sender.sendMessage(ChatColor.RED + "Username cannot be blank.");
                return;
            }
            String playerUUID = Default.UUIDZero;
            OfflinePlayer op = server.getOfflinePlayerIfCached(username);
            if(op != null)
                if (op.hasPlayedBefore())
                    playerUUID = op.getUniqueId().toString();

            if(username.equals("Admin"))
            {
                playerUUID = Default.UUIDOne;
            }

            List<Waystone> ways = Work.FilterToUser(context, playerUUID);

            if(ways.size() == 0)
            {
                sender.sendMessage(ChatColor.RED + "The user, '" + username + "', does not have a waystone accessible to us.");
                return;
            }

            if(args.length >= 3)
            {
                String waystoneName = args[2];
                if(waystoneName.trim().length() == 0)
                {
                    sender.sendMessage(ChatColor.RED + "Waystone name cannot be blank.");
                    return;
                }
                Waystone way = null;
                for(Waystone wei: ways)
                    if(wei.name.equalsIgnoreCase(waystoneName)) {
                        way = wei;
                        break;
                    }
                if(way == null)
                {
                    sender.sendMessage(ChatColor.RED + "Could not access Waystone '" + waystoneName + "'.");
                    return;
                }
                if(!way.canUse())
                {
                    p.sendMessage(ChatColor.RED + "Destination Waystone is still charging. " + way.timeLeftUntilFunctional()
                            + " seconds left until usable.");
                    return;
                }
                Waystone origin = null;
                if(WeiPlayer.InWaystoneUse)
                {
                    origin = WeiPlayer.LastVisited;
                }
                events.OnTeleport(p, way, origin);
            } else {
                sender.sendMessage(ChatColor.RED + "You must provide a Waystone to teleport to.\n" + ChatColor.GOLD +
                        "/waystone teleport <owner's name> <waystone name>");
                return;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must provide a username whom owns a waystone.\n" + ChatColor.GOLD +
                    "/waystone teleport <owner's name> <waystone name>");
            return;
        }

    }
}
