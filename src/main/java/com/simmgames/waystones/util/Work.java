package com.simmgames.waystones.util;

import com.simmgames.waystones.Accessibility;
import com.simmgames.waystones.data.Data;
import com.simmgames.waystones.data.WayPlayer;
import com.simmgames.waystones.data.Waystone;

import com.simmgames.waystones.structure.Vector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.util.Vector;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Work {
    public static Location FindBlockType(int searchDistance, Location playerLocation, Material type)
    {
        Vector3 origin = new Vector3(playerLocation);
        List<Vector3> searchThis = searchFromOrigin(origin, searchDistance);

        for(Vector3 block: searchThis)
        {
            double dist = (origin).getDistance(block);
            if(dist < searchDistance * searchDistance)
            {
                Location l = new Location(playerLocation.getWorld(), block.X, block.Y+2, block.Z);
                if(l.getBlock().getType() == type)
                    return l;
            }
        }
        return null;
    }
    public static UUID CreateHologram(Location location, String text, boolean visable)
    {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location.add(new Vector(0.5, -1, 0.5)), EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setCustomNameVisible(visable);
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

    public static void DestroyHologram(Location location, UUID uuid)
    {
        ArmorStand armorStand = (ArmorStand)location.getWorld().getEntity(uuid);
        if(armorStand == null)
        {
            return;
        }
        armorStand.remove();
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

        for(Vector3 block: searchThis)
        {
            Location feet = new Location(world, block.X, block.Y, block.Z);
            Location head = new Location(world, block.X, block.Y + 1, block.Z);
            Location floor = new Location(world, block.X, block.Y - 1, block.Z);

            boolean floorOkay = floor.getBlock().getType().isSolid();
            boolean feetOkay = feet.getBlock().getType().isAir();
            boolean headOkay = head.getBlock().getType().isAir();

            if(floorOkay && feetOkay && headOkay)
                return feet.add(0.5, 0, 0.5);
        }
        return null;
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
        int highest = -1;
        for(PermissionAttachmentInfo pai : permissible.getEffectivePermissions())
        {
            if(pai.getPermission().startsWith(baseNode + "."))
            {
                String working = pai.getPermission().substring((baseNode + ".").length());

                try {
                    highest = Integer.parseInt(working.trim());
                } catch (Exception e) {
                    continue;
                }
            }
        }
        if(highest == -1)
            highest = defaultInt;
        return highest;
    }
}
