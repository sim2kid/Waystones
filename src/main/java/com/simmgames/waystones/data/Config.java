package com.simmgames.waystones.data;

import com.simmgames.waystones.permissions.Perm;
import com.simmgames.waystones.util.Work;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    public static JavaPlugin plugin;
    public static Data data;
    public static void Setup(JavaPlugin plugin, Data data)
    {
        Config.plugin = plugin;
        Config.data = data;
    }

    public static double WaystoneUseDistance()
    {
        return Math.max(plugin.getConfig().getInt("use-distance"), 1);
    }

    public static double WaystoneDiscoverDistance()
    {
        return Math.max(plugin.getConfig().getInt("discover-distance"), -1);
    }

    public static double WaystoneNearDistance()
    {
        return Math.max(plugin.getConfig().getInt("near-distance"), 0);
    }

    public static int LodestoneSearchRadius()
    {
        return Math.min(Math.max(plugin.getConfig().getInt("search-radius-lode"), 0), 15);
    }

    public static int TPSearchRadius()
    {
        return Math.max(plugin.getConfig().getInt("search-radius-tp"), 1);
    }

    public static int WaystoneCreationLimit(Player p)
    {
        int limit = Math.max(plugin.getConfig().getInt("creation-limit"), -1);
        int toReturn = Work.GetMaxNumFromPermissions("waystone.create.limit", limit, p);
        if(limit == -1)
            toReturn = Integer.MAX_VALUE;
        return toReturn;
    }

    public static int WaystoneChargeTime(Player p)
    {
        if(p.hasPermission(Perm.ChargeBypass))
            return 0;
        int exponent = Work.FilterToUser(data.AllWaystones, p.getUniqueId().toString()).size();
        int base = plugin.getConfig().getInt("charge-time");
        double figure = plugin.getConfig().getDouble("charge-figure");
        double left = Math.max(plugin.getConfig().getDouble("exponent-left"),0);
        boolean useDim = plugin.getConfig().getBoolean("use-diminishing-returns");
        boolean flip = plugin.getConfig().getBoolean("flip-figure-and-exponent");

        if(!useDim)
        {
            int lowest = Work.GetMinNumFromPermissions("waystone.charge", base, p);
            return Math.max(lowest, 0);
        }

        double expoArea = exponent + left;

        if(flip)
            return (int)(base + Math.pow(expoArea, figure));
        else
            return (int)(base + Math.pow(figure, expoArea));
    }
    public static boolean NoGrief()
    {
        return plugin.getConfig().getBoolean("no-grief");
    }
    public static boolean DefaultNametag()
    {
        return plugin.getConfig().getBoolean("default-nametag");
    }
    public static String DefaultAccess()
    {
        return plugin.getConfig().getString("default-access");
    }
    public static int DefaultListSize()
    {
        return plugin.getConfig().getInt("default-list-size");
    }

    public static boolean UseKarma()
    {
        return plugin.getConfig().getBoolean("teleportation-karma");
    }
    public static int KarmaLimit()
    { return plugin.getConfig().getInt("karma-limit"); }
    public static double KarmaStrength()
    { return plugin.getConfig().getDouble("karma-strength"); }

    public static boolean UseShortCircuit()
    { return plugin.getConfig().getBoolean("waystone-short-circuit"); }
    public static double ShortCircuitChance()
    { return plugin.getConfig().getDouble("waystone-short-circuit-chance"); }
    public static boolean ShortCircuitIgnoreAdmin()
    { return plugin.getConfig().getBoolean("short-circuit-ignore-adminstones"); }
    public static int ShortCircuitDuration()
    { return plugin.getConfig().getInt("waystone-short-circuit-duration"); }
    public static int ShortCircuitSpread()
    { return plugin.getConfig().getInt("waystone-short-circuit-spread"); }

    public static boolean SpawnEndermites()
    { return plugin.getConfig().getBoolean("spawn-endermites"); }
    public static double EndermiteChance()
    { return plugin.getConfig().getDouble("spawn-endermites-chance"); }

    public static boolean CustomItems()
    {
        return UseWarpScroll() || UseWarpCrystal();
    }
    public static boolean UseWarpScroll()
    {
        return plugin.getConfig().getBoolean("use-warpscroll");
    }
    public static boolean UseWarpCrystal()
    {
        return plugin.getConfig().getBoolean("use-warpcrystal");
    }
    public static boolean UseWarpItemOnFail()
    {
        return plugin.getConfig().getBoolean("use-warp-on-fail");
    }
    public static int WarpCrystalMax() {
        return plugin.getConfig().getInt("warpcrystal-max-uses");
    }
    public static double WarpCrystalPercent()
    { return plugin.getConfig().getDouble("warpcrystal-max-percent"); }
}
