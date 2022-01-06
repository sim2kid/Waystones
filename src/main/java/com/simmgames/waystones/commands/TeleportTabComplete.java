package com.simmgames.waystones.commands;

import com.simmgames.waystones.data.Config;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.util.Work;
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

public class TeleportTabComplete implements TabCompleter {
    Logger out;
    Data data;
    WaystoneBlockEvents events;
    Server server;

    public TeleportTabComplete(Logger pluginLogger, Data data, WaystoneBlockEvents events, JavaPlugin plugin)
    {
        out = pluginLogger;
        this.data = data;
        this.events = events;
        server = plugin.getServer();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        args = Work.PreProcessArgs(args);
        List<String> toReturn = new ArrayList<String>();
        if(!sender.hasPermission(Perm.Teleport))
            return toReturn;

        if(!(sender instanceof Player))
            return toReturn;
        Player p = (Player)sender;
        WayPlayer wp = data.GrabPlayer(p.getUniqueId().toString());

        if(!sender.hasPermission(Perm.TeleportIgnoreWaystone)) {
            if (wp.LastVisited != null) {
                if (!(wp.LastVisited.canUse() && wp.InWaystoneUse)) {
                    return toReturn;
                }
            } else {
                return toReturn;
            }
        }

        List<Waystone> context;
        if(sender.hasPermission(Perm.TeleportUnknown))
            context = Work.GetKnownAndUnknownWaystones(p, data);
        else if(sender.hasPermission(Perm.TeleportAll))
            context = Work.GetKnownWaystones(p, data);
        else
            context = data.AllWaystones;

        String username = null;
        String waystone = "";

        if(args.length >= 1)
        {

            String UserStone = args[0];
            // Breakup namespaced waystones
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
                }
                else
                {
                    username = strs[0];
                }
            }
            else
            {
                waystone = UserStone;
            }
        }

        if(args.length == 1)
        {
            // all possible waystones based on updated context
            for(Waystone wei: context)
                if(wei.canUse()) {
                    if(username == null && waystone.length() > 0 ) {
                        if (!(Work.PlayerUUIDtoUser(wei.owner, server).toLowerCase().startsWith(waystone.toLowerCase()) ||
                                wei.name.toLowerCase().startsWith(waystone.toLowerCase())))
                            continue;
                    }
                    else if(username != null)
                    {
                        // use username and cull waystones
                        if (!Work.PlayerUUIDtoUser(wei.owner, server).equalsIgnoreCase(username))
                            continue;
                        if (!wei.name.toLowerCase().startsWith(waystone.toLowerCase()))
                            continue;
                    }

                    toReturn.add("" + Work.PlayerUUIDtoUser(wei.owner, server) + ":" + wei.name);
                }
        }
        if(args.length == 2 && username == null)
        {
            username = args[1];

            // Find waystones in context
            List<Waystone> updatedContext = new ArrayList<>();
            for(Waystone wei: context)
                if(wei.name.equalsIgnoreCase(waystone) && wei.canUse())
                    updatedContext.add(wei);

            // If username was blank, list it now
            for(Waystone wei: updatedContext)
                if(wei.name.equalsIgnoreCase(waystone))
                    if(wei.canUse())
                    {
                        // cull based on usernames alone
                        if (!Work.PlayerUUIDtoUser(wei.owner, server).toLowerCase().startsWith(username.toLowerCase()))
                            continue;
                        toReturn.add(Work.PlayerUUIDtoUser(wei.owner, server));
                    }
        }

        // Preprocess spaces
        for(int i = 0; i < toReturn.size(); i++)
        {
            if(toReturn.get(i).contains(" "))
                toReturn.set(i, "\"" + toReturn.get(i) + "\"");
        }

        return toReturn;
    }
}
