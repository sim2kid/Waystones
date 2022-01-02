package com.simmgames.waystones.commands;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.util.Default;
import com.simmgames.waystones.util.Work;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
            if(sender.hasPermission(Perm.Teleport)) {
                if(!(sender instanceof Player))
                    return toReturn;
                Player p = (Player)sender;
                WayPlayer wp = data.GrabPlayer(p.getUniqueId().toString());
                if(wp.InWaystoneUse || sender.hasPermission(Perm.TeleportIgnoreWaystone)) {
                    toReturn.add("teleport");
                    toReturn.add("tp");
                }
            }
            toReturn.add("help");
            if(sender.hasPermission(Perm.Create))
                toReturn.add("create");
            if(sender.hasPermission(Perm.Nametag))
                toReturn.add("nametag");
            if(sender.hasPermission(Perm.List))
                toReturn.add("list");
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
            else if(args[0].equalsIgnoreCase("list") && sender.hasPermission(Perm.List))
            {
                if(args.length == 2)
                {
                    toReturn.add("known");
                    toReturn.add("public");
                    toReturn.add("mine");
                    if(sender.hasPermission(Perm.ListUnknown))
                        toReturn.add("unknown");
                    if(sender.hasPermission(Perm.ListAll))
                        toReturn.add("all");
                }
            } else if(args[0].equalsIgnoreCase("teleport") && sender.hasPermission(Perm.Teleport))
            {
                if(!(sender instanceof Player))
                    return toReturn;
                Player p = (Player)sender;
                WayPlayer wp = data.GrabPlayer(p.getUniqueId().toString());
                if(!(wp.InWaystoneUse || sender.hasPermission(Perm.TeleportIgnoreWaystone)))
                    return toReturn;


                List<Waystone> context;
                if(sender.hasPermission(Perm.TeleportUnknown))
                    context = Work.GetKnownAndUnknownWaystones(p, data);
                else if(sender.hasPermission(Perm.TeleportAll))
                    context = Work.GetKnownWaystones(p, data);
                else
                    context = data.AllWaystones;

                if(args.length == 2)
                {
                    // usernames
                    for(Waystone wei: context)
                    {
                        String username = data.GrabPlayer(wei.owner).lastUsername;
                        if(!toReturn.contains(username))
                            toReturn.add(username);
                    }
                }
                if(args.length == 3)
                {
                    String username = args[1];
                    if(username.trim().length() == 0)
                        return toReturn;
                    String playerUUID = Default.UUIDZero;
                    OfflinePlayer op = server.getOfflinePlayerIfCached(username);
                    if(op != null)
                        if (op.hasPlayedBefore())
                            playerUUID = op.getUniqueId().toString();

                    if(username.equals("Admin"))
                    {
                        playerUUID = Default.UUIDOne;
                    }
                    for(Waystone wei: Work.FilterToUser(context, playerUUID))
                        toReturn.add(wei.name);
                }
            } else if(args[0].equalsIgnoreCase("tp") && sender.hasPermission(Perm.Teleport))
            {
                if(!(sender instanceof Player))
                    return toReturn;
                Player p = (Player)sender;
                WayPlayer wp = data.GrabPlayer(p.getUniqueId().toString());
                if(!(wp.InWaystoneUse || sender.hasPermission(Perm.TeleportIgnoreWaystone)))
                    return toReturn;

                List<Waystone> context = Work.GetOwnAndPublicWaystones(p, data);

                if(args.length == 2)
                {
                    // locations
                    for(Waystone wei: context)
                        toReturn.add(wei.name);
                }
            }
        }
        return toReturn;
    }
}
