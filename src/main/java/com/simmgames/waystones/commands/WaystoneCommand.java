package com.simmgames.waystones.commands;

import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.*;
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
        args = Work.PreProcessArgs(args);
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
            } else if(args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport"))
            {
                teleport(sender, args);
                return true;
            }


            if(sender instanceof Player)
            {
                help(sender, args);
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
            message += ChatColor.GOLD + "/waystone teleport <[Creator:]<Waystone>> [Creator]" + ChatColor.AQUA + " Teleport to an accessible Waystone.";
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
        if(!(waystoneCount < Config.WaystoneCreationLimit(p) || p.hasPermission(Perm.CreateBypass) || p.hasPermission(Perm.CreateAdmin)))
        {
            sender.sendMessage(ChatColor.RED + "You have reached your max number of Waystones that you can create. ["
                    + waystoneCount + "/" + Config.WaystoneCreationLimit(p) + "].");
            return;
        }


        // Check if there is a lodestone nearby
        Location lode = Work.FindBlockType(Config.LodestoneSearchRadius(), p.getLocation(), Material.LODESTONE);
        if(lode == null)
        {
            p.sendMessage( ChatColor.RED + "No Lodestone nearby to turn into a Waystone. Make sure you are within " +
                    Config.LodestoneSearchRadius() + " blocks.");
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
        if(!(waystoneCount < Config.WaystoneCreationLimit(p) || p.hasPermission(Perm.CreateBypass)) && !admin )
        {
            sender.sendMessage(ChatColor.RED + "You have reached your max number of Waystones that you can create. ["
                    + waystoneCount + "/" + Config.WaystoneCreationLimit(p) + "].");
            return;
        }

        Accessibility access = null;

        String accessStr = Config.DefaultAccess();
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

        int chargeTime = Config.WaystoneChargeTime(p);
        if(admin)
            chargeTime = 0;

        // Create Waystone
        Waystone newWaystone = new Waystone((admin ? Default.UUIDOne : p.getUniqueId().toString()),
                new BlockLocation(lode), waystoneName.trim(), access, chargeTime, Config.DefaultNametag());
        if(newWaystone.hasNametag)
            newWaystone.hologramUUID = Work.CreateHologram(lode.getBlock().getLocation(), newWaystone.decodeName(data)).toString();
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
        WayPlayer wp = data.GrabPlayer(p.getUniqueId().toString());

        // check if waystone is nearby
        Waystone wei = wp.Closest();
        if(wp.LastNear == null)
        {
            p.sendMessage(ChatColor.RED + "No Waystone nearby. Make sure you are within " + Config.LodestoneSearchRadius()
                    + " blocks of one.");
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
            // Toggle
            wei.hasNametag = !wei.hasNametag;

            if(!Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), wei.hasNametag) && wei.hasNametag)
                wei.hologramUUID = Work.CreateHologram(wei.location.getLocation(server), wei.decodeName(data)).toString();
            if(wei.hasNametag)
                p.sendMessage(ChatColor.GREEN + "Turning on the NameTag for the nearest waystone.");
            else
                p.sendMessage(ChatColor.GREEN + "Turning off the NameTag for the nearest waystone.");
            return;
        } else if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("t") ||
                args[1].equalsIgnoreCase("on"))
        {
            // turn on
            wei.hasNametag = true;
            p.sendMessage(ChatColor.GREEN + "Turning on the NameTag for the nearest waystone.");
            if(!Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), true))
                wei.hologramUUID = Work.CreateHologram(wei.location.getLocation(server), wei.decodeName(data)).toString();
        } else if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("f") ||
                args[1].equalsIgnoreCase("off"))
        {
            // turn off
            wei.hasNametag = false;
            p.sendMessage(ChatColor.YELLOW + "Turning off the NameTag for the nearest waystone.");
            Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), false);
        } else if(args[1].equalsIgnoreCase("toggle") || args[1].equalsIgnoreCase("flip")
                || args[1].equalsIgnoreCase("switch")) {
            // Toggle
            wei.hasNametag = !wei.hasNametag;

            if(!Work.HologramVisibility(wei.location.getLocation(server), UUID.fromString(wei.hologramUUID), wei.hasNametag) && wei.hasNametag)
                wei.hologramUUID = Work.CreateHologram(wei.location.getLocation(server), wei.decodeName(data)).toString();
            if(wei.hasNametag)
                p.sendMessage(ChatColor.GREEN + "Turning on the NameTag for the nearest waystone.");
            else
                p.sendMessage(ChatColor.GREEN + "Turning off the NameTag for the nearest waystone.");
            return;
        }
        else
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

        String whatToShow = "default";
        if(args.length >= 2)
        {
            whatToShow = args[1];
        }

        String username = "";
        if(args.length >= 3)
        {
            username = args[2];
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
            case "available":
            case "known":
            case "public":
            case "mine":
            case "unknown":
            case "all":
                List<String> strings = TrimList(data.AllWaystones, whatToShow, WeiPlayer, p, sender);
                if(strings == null)
                    return;
                Results.addAll(strings);
                break;
            case "player":
                if(username.trim().length() == 0){
                    sender.sendMessage(ChatColor.RED + "To list player's Waystones, you need to provide a player username.");
                    return;
                }
                String playerUUID = Default.UUIDZero;
                OfflinePlayer op = server.getOfflinePlayerIfCached(username);
                if(op != null)
                    if (op.hasPlayedBefore())
                        playerUUID = op.getUniqueId().toString();
                if(username.equalsIgnoreCase("Admin"))
                    playerUUID = Default.UUIDOne;
                if(Default.UUIDZero.equalsIgnoreCase(playerUUID))
                {
                    p.sendMessage(ChatColor.RED + "The user '" + username + "' has never played on this server.");
                    return;
                }
                List<Waystone> trimmedStones = Work.FilterToUser(data.AllWaystones, playerUUID);

                if(trimmedStones.size() == 0)
                {
                    p.sendMessage(ChatColor.RED + "User '" + username + "' doesn't have any Waystones.");
                    return;
                }

                String playerFilter = "default";
                if(args.length >= 4)
                {
                    playerFilter = args[3];
                }

                Results = TrimList(trimmedStones, playerFilter, WeiPlayer, p, sender);
                if(Results == null) {
                    return;
                }

                break;
            default:
                sender.sendMessage(ChatColor.RED + "'" + whatToShow + "' is an unknown thing to list.");
                return;
        }

        int perList = Config.DefaultListSize();

        int max = (int) Math.ceil((double) Results.size() / (double) perList);

        page = (int) Math.min(max, Math.max(page, 1));

        Collections.sort(Results);

        String filler = "==========";
        whatToShow = whatToShow.substring(0,1).toUpperCase() + whatToShow.substring(1).toLowerCase();
        String headFoot = ChatColor.RESET + filler + " " + whatToShow + " Waystones " + page + "/" + max + " " + filler;
        String workingString = headFoot + "\n";

        if(Results.size() == 0)
            workingString += ChatColor.LIGHT_PURPLE + "No Results\n";
        else
            for(int i = (page-1)*perList; i < (int)Math.min(page*perList,Results.size()); i++)
                workingString += Results.get(i);

        workingString += headFoot;

        sender.sendMessage(workingString);
    }

    public List<String> TrimList(List<Waystone> inputList, String trimTo, WayPlayer WeiPlayer, Player p, CommandSender sender)
    {
        List<String> Results = new ArrayList<>();
        switch (trimTo.trim().toLowerCase()) {
            case "default":
            case "available":
                for(Waystone wei: inputList) {
                    if(!(wei.access == Accessibility.Public || WeiPlayer.KnownWaystones.contains(wei)))
                        continue;
                    if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
                        continue;
                    if(!wei.canUse())
                        continue;
                    Results.add(Work.DecorateWaystoneName(wei,p,data,server));
                }
                break;
            case "known":
                for(Waystone wei: inputList) {
                    if(wei.access == Accessibility.Public || WeiPlayer.KnownWaystones.contains(wei))
                        Results.add(Work.DecorateWaystoneName(wei,p,data,server));
                }
                break;
            case "public":
                for(Waystone wei: inputList)
                    if (wei.access == Accessibility.Public) {
                        Results.add(Work.DecorateWaystoneName(wei,p,data,server));
                    }
                break;
            case "mine":
                for(Waystone wei: inputList)
                    if (wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
                    {
                        Results.add(Work.DecorateWaystoneName(wei,p,data,server));
                    }
                break;
            case "unknown":
                if(!sender.hasPermission(Perm.ListUnknown))
                {
                    sender.sendMessage(Local.NoPermsCommand());
                    return null;
                }
                for(Waystone wei: inputList) {
                    if(WeiPlayer.KnownWaystones.contains(wei) || wei.access != Accessibility.Discoverable)
                        continue;
                    Results.add(Work.DecorateWaystoneName(wei,p,data,server));
                }
                break;
            case "all":
                if(!sender.hasPermission(Perm.ListAll))
                {
                    sender.sendMessage(Local.NoPermsCommand());
                    return null;
                }
                for(Waystone wei: inputList) {
                    Results.add(Work.DecorateWaystoneName(wei,p,data,server));
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "'" + trimTo + "' is an unknown thing to list.");
                return null;
        }
        return Results;
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
            if(WeiPlayer.LastVisited.timeLeftUntilFunctional() > 0)
                p.sendMessage(ChatColor.RED + "Origin Waystone is still charging. T-" + WeiPlayer.LastVisited.FormattedTimeLeft()
                        + " left until usable.");
            else
                p.sendMessage(ChatColor.RED + "Origin Waystone Unavailable");
            return;
        }

        List<Waystone> context;
        if(sender.hasPermission(Perm.TeleportUnknown))
            context = Work.GetKnownAndUnknownWaystones(p, data);
        else if(sender.hasPermission(Perm.TeleportAll))
            context = Work.GetKnownWaystones(p, data);
        else
            context = data.AllWaystones;


        if(args.length >= 2) {
            String UserStone = args[1];

            // Breakup namespaced waystones
            String username = null;
            String waystone = "";
            if(UserStone.contains(":"))
            {
                String[] strs = UserStone.split(":");
                if(strs.length > 1)
                {
                    username = strs[0];
                    waystone = "";
                    for (int i = 1; i < strs.length; i++)
                        waystone += strs[i] + " ";
                    waystone = waystone.substring(0, waystone.length()-1);
                } else
                {
                    waystone = UserStone;
                }
            }
            else
            {
                waystone = UserStone;
            }

            if(args.length >= 3 && username == null)
            {
                username = args[2];
            }

            // Fill in username if possible
            if(username != null)
                context = Work.FilterToUser(context, Work.PlayerUserToUUID(username, server));


            // Find waystones in context
            List<Waystone> stones = new ArrayList<>();
            for(Waystone wei: context)
            {
                if(wei.name.equalsIgnoreCase(waystone))
                {
                    if(wei.canUse())
                    {
                        if(username != null)
                            if(!Work.PlayerUUIDtoUser(wei.owner, server).equalsIgnoreCase(username))
                                continue;
                        stones.add(wei);
                    }
                }
            }

            // Get the origin stone
            Waystone origin = null;
            if(WeiPlayer.InWaystoneUse)
            {
                origin = WeiPlayer.LastVisited;
            }

            // results
            if(stones.size() == 0)
            {
                // No Stones to TP to
                if(username != null)
                {
                    // User doesn't have a waystone in this world
                    sender.sendMessage(ChatColor.RED + "Could not find/access Waystone '" + waystone + "' built by " + username + ".");
                } else {
                    // Waystone can not be found
                    sender.sendMessage(ChatColor.RED + "Could not find/access Waystone '" + waystone + "'.");
                }
            }
            else if(stones.size() == 1)
            {
                // TP to this stone
                events.OnTeleport(p, stones.get(0), origin);
            } else {
                // Multiple stones to distinguish from
                Waystone myWaystone = null;
                for(Waystone accessStones : stones)
                    if(accessStones.owner.equalsIgnoreCase(WeiPlayer.UUID))
                        myWaystone = accessStones;

                if(myWaystone != null)
                {
                    events.OnTeleport(p, myWaystone, origin);
                    return;
                }
                sender.sendMessage(ChatColor.RED + "You need to specify who's waystone to teleport to.\n" + ChatColor.GOLD +
                        "/teleport <[Creator:]<Waystone>> [Creator]");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "You must provide a waystone to teleport to.\n" + ChatColor.GOLD +
                    "/waystone teleport <[Creator:]<Waystone>> [Creator]");
        }
    }
}
