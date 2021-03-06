package com.simmgames.waystones.util;

import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;

import com.simmgames.waystones.structure.Vector3;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.Vector;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Material.*;

public class Work {
    public static Location FindBlockType(int searchDistance, Location playerLocation, Material type)
    {
        Vector3 origin = new Vector3(playerLocation);
        List<Vector3> searchThis = searchFromOrigin(origin, searchDistance);

        Location closest = null;
        double shortest = Double.MAX_VALUE;

        for(Vector3 block: searchThis)
        {
            double dist = (origin).getDistance(block);
            if(dist < searchDistance * searchDistance)
            {
                Location l = new Location(playerLocation.getWorld(), block.X, block.Y+2, block.Z);
                if(l.getBlock().getType() == type) {
                    double distance = (new Vector3(playerLocation)).getDistance(new Vector3(l));
                    if(shortest > distance)
                    {
                        shortest = distance;
                        closest = l;
                    }
                }
            }
        }
        return closest;
    }
    public static UUID CreateHologram(Location location, String text)
    {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location.add(new Vector(0.5, -1, 0.5)), EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(text);

        hologram.setGravity(false);
        hologram.setInvulnerable(true);
        hologram.setCanMove(false);

        return hologram.getUniqueId();
    }

    public static boolean UpdateHologram(Location location, UUID uuid, String text)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return false;
        }
        armorStand.setCustomName(text);
        return true;
    }

    public static boolean HologramVisibility(Location location, UUID uuid, boolean visibility)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return false;
        }
        armorStand.setCustomNameVisible(visibility);
        return true;
    }

    public static boolean DestroyHologram(Location location, UUID uuid)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return false;
        }
        armorStand.remove();
        return true;
    }
    public static boolean DestroyUnmarkedHolograms(Location location)
    {
        boolean canReturnTrue = false;
        Collection<Entity> collection = location.getWorld().getNearbyEntities(
                location.add(new Vector(0.5, -1, 0.5)), 0.5, 0.5, 0.5);
        for(Entity e: collection) {
            ArmorStand armorStand = (ArmorStand)e;
            if (armorStand == null) {
                return false;
            }
            armorStand.remove();
            canReturnTrue = true;
        }
        return canReturnTrue;
    }

    public static List<Waystone> GetOwnAndPublicWaystones(Player player, Data data)
    {
        // public and mine
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        List<Waystone> ways = new ArrayList<Waystone>();

        for (Waystone wei: data.AllWaystones)
            if(wei.access == Accessibility.Public || wei.owner.equalsIgnoreCase(p.UUID))
                if(wei.location.WorldUUID.equalsIgnoreCase(player.getWorld().getUID().toString()))
                    ways.add(wei);

        return ways;
    }
    public static List<Waystone> GetKnownWaystones(Player player, Data data)
    {
        // public and known
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        List<Waystone> ways = new ArrayList<Waystone>();

        for (Waystone wei: data.AllWaystones)
            if(wei.access == Accessibility.Public || p.KnownWaystones.contains(wei))
                if(wei.location.WorldUUID.equalsIgnoreCase(player.getWorld().getUID().toString()))
                    ways.add(wei);

        return ways;
    }
    public static List<Waystone> GetKnownAndUnknownWaystones(Player player, Data data)
    {
        // public and Discoverable
        WayPlayer p = data.GrabPlayer(player.getUniqueId().toString());
        List<Waystone> ways = new ArrayList<Waystone>();

        for (Waystone wei: data.AllWaystones)
            if(wei.access == Accessibility.Public || wei.owner.equalsIgnoreCase(p.UUID) ||
                    wei.access == Accessibility.Discoverable)
                if(wei.location.WorldUUID.equalsIgnoreCase(player.getWorld().getUID().toString()))
                    ways.add(wei);

        return ways;
    }
    public static List<Waystone> FilterToUser(List<Waystone> source, String playerUUID)
    {
        List<Waystone> ways = new ArrayList<Waystone>();
        for (Waystone wei: source)
            if(wei.owner.equalsIgnoreCase(playerUUID))
                ways.add(wei);
        return ways;
    }

    public static Location FindSafeTP(Location origin, int searchDistance)
    {
        World world = origin.getWorld();
        List<Vector3> searchThis = searchFromOrigin(new Vector3(origin), searchDistance);

        Location closest = null;
        double shortest = Double.MAX_VALUE;

        for(Vector3 block: searchThis)
        {
            Location feet = new Location(world, block.X, block.Y, block.Z);
            Location head = new Location(world, block.X, block.Y + 1, block.Z);
            Location floor = new Location(world, block.X, block.Y - 1, block.Z);

            boolean floorOkay = floor.getBlock().getType().isSolid();
            boolean feetOkay = feet.getBlock().getType().isAir();
            boolean headOkay = head.getBlock().getType().isAir();

            if(floorOkay && feetOkay && headOkay) {
                Location result = feet.add(0.5, 0, 0.5);
                double distance = (new Vector3(origin)).getDistance(new Vector3(result));
                if(distance < shortest) {
                    closest = result;
                    shortest = distance;
                }
            }
        }
        return closest;
    }

    public static List<Vector3> searchFromOrigin(Vector3 origin, int radius)
    {
        List<Vector3> list = new ArrayList<Vector3>();
        int y = (int)origin.Y;
        for(int yc = 0; yc < radius*2; yc++) {

            int x = (int)origin.X;
            for(int xc = 0; xc < radius*2; xc++) {
                int z = (int)origin.Z;
                for (int zc = 0; zc < radius*2; zc++) {
                    list.add(new Vector3(x,y,z));
                    z += (zc%2 == 0) ? -zc : zc;
                }
                x += (xc%2 == 0) ? -xc : xc;
            }

            y += (yc%2 == 0) ? -yc : yc;
        }
        return list;
    }

    public static int GetMaxNumFromPermissions(String baseNode, int defaultInt, Permissible permissible)
    {
        if(permissible.isOp())
            return -1;
        int highest = Integer.MIN_VALUE;
        for(PermissionAttachmentInfo pai : permissible.getEffectivePermissions())
        {
            if(pai.getPermission().startsWith(baseNode + "."))
            {
                String working = pai.getPermission().substring((baseNode + ".").length());

                try {
                    int out = Integer.parseInt(working.trim());
                    highest = Math.max(out, highest);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        if(highest == Integer.MIN_VALUE)
            highest = defaultInt;
        return highest;
    }

    public static int GetMinNumFromPermissions(String baseNode, int defaultInt, Permissible permissible)
    {
        if(permissible.isOp())
            return -1;
        int lowest = Integer.MAX_VALUE;
        for(PermissionAttachmentInfo pai : permissible.getEffectivePermissions())
        {
            if(pai.getPermission().startsWith(baseNode + "."))
            {
                String working = pai.getPermission().substring((baseNode + ".").length());

                try {
                    int out = Integer.parseInt(working.trim());
                    lowest = Math.min(out, lowest);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        if(lowest == Integer.MAX_VALUE)
            lowest = defaultInt;
        return lowest;
    }

    public static String DecorateWaystoneName(Waystone wei, Player p, Data data, Server server)
    {
        WayPlayer WeiPlayer = data.GrabPlayer(p.getUniqueId().toString());
        String otherWorld = "";
        if(!wei.location.WorldUUID.equalsIgnoreCase(p.getWorld().getUID().toString()))
            otherWorld = ChatColor.DARK_GRAY + "[" + wei.location.getLocation(server).getWorld().getName() + "]";
        if(!wei.canUse())
            otherWorld += ChatColor.DARK_RED + "[Unavailable]";
        if (wei.access == Accessibility.Public){
            return(otherWorld + ChatColor.BLUE + "[Public] " + ChatColor.AQUA + wei.name + "\n");
        }
        else if(wei.access == Accessibility.Private)
        {
            if(wei.owner.equalsIgnoreCase(WeiPlayer.UUID))
            {
                return(otherWorld + ChatColor.GOLD + "[Private] " + ChatColor.AQUA + wei.name + "\n");
            }
            else
            {
                return(otherWorld + ChatColor.GOLD + "[Private]" + ChatColor.DARK_PURPLE +
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
            return(otherWorld + player + unknown + ChatColor.AQUA + wei.name + "\n");
        }
    }

    public static boolean IsIntractable(Material blockType)
    {
        boolean intractable = blockType.isInteractable();
        if (!intractable)
            return false;
        if(Tag.STAIRS.isTagged(blockType) || Tag.FENCES.isTagged(blockType))
            return false;
        switch (blockType)
        {
            case REDSTONE_ORE:
            case REDSTONE_WIRE:
            case PUMPKIN:
            case PISTON_HEAD:
            case MOVING_PISTON:
                return false;
            default:
                return true;
        }
    }

    public static String[] PreProcessArgs(String[] args)
    {
        List<String> strList = new ArrayList<>();
        for(int i = 0; i < args.length; i++)
        {
            if(args[i] == null)
            {
                continue;
            }
            if(args[i].startsWith("\"") && args[i].length() > 1)
            {
                int end;
                boolean succeeded = false;
                String UseThis = "";
                for(end = i; end < args.length; end++)
                {
                    boolean skipSub = false;
                    if(args[end].endsWith("\\\""))
                    {
                        skipSub = true;

                    }
                    UseThis += args[end] + " ";
                    if(args[end].endsWith("\"") && !skipSub)
                    {
                        UseThis = UseThis.substring(0, UseThis.length()-2);
                        break;
                    }
                }
                i = end;
                UseThis = UseThis.substring(1);
                UseThis = UseThis.replace("\\\"", "\"");
                strList.add(UseThis);
            } else {
                if(args[i].startsWith("\\\"") && args[i].length() > 2)
                    args[i] = args[i].substring(1);
                strList.add((args[i]));
            }
        }
        String[] array = new String[strList.size()];
        for(int i = 0; i < array.length; i++)
            array[i] = strList.get(i);
        return array;
    }

    public static String PlayerUserToUUID(String username, Server server)
    {
        String playerUUID = Default.UUIDZero;
        OfflinePlayer op = server.getOfflinePlayerIfCached(username);
        if(op != null)
            if (op.hasPlayedBefore())
                playerUUID = op.getUniqueId().toString();

        if(username.equals("Admin"))
        {
            playerUUID = Default.UUIDOne;
        }
        return playerUUID;
    }

    public static String PlayerUUIDtoUser(String uuid, Server server)
    {
        String username = "Unknown";
        OfflinePlayer op = server.getOfflinePlayer(UUID.fromString(uuid));
        if(op != null)
            if (op.hasPlayedBefore())
                username = op.getName();

        if(uuid.equals(Default.UUIDOne))
        {
            username = "Admin";
        }
        return username;
    }

    public static Waystone GetWaystoneAt(Location location, Data data)
    {
        for(Waystone wei : data.AllWaystones)
        {
            if(!wei.location.WorldUUID.equalsIgnoreCase(location.getWorld().getUID().toString()))
                continue;
            if(wei.location.Position.equals(new Vector3(location)))
                return wei;
        }
        return null;
    }
}
