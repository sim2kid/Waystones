package com.simmgames.waystones.commands;

import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.Local;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.events.WaystoneBlockEvents;
import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.util.Work;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeleportCommand implements CommandExecutor {
    Logger out;
    Data data;
    WaystoneBlockEvents events;
    Server server;

    public TeleportCommand(Logger pluginLogger, Data data, WaystoneBlockEvents events, JavaPlugin plugin)
    {
        out = pluginLogger;
        this.data = data;
        this.events = events;
        server = plugin.getServer();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        args = Work.PreProcessArgs(args);
        if(sender.hasPermission(Perm.CommandTeleport) && sender.hasPermission(Perm.Teleport))
            teleport(sender, args);
        return true;
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


        if(args.length >= 1) {
            String UserStone = args[0];

            if(args.length >= 2)
            {

            }

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

            if(args.length >= 2 && username == null)
            {
                username = args[1];
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
