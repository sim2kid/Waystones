package com.simmgames.waystones.permissions;

import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

public class Perm {
    public static Permission Default;
    public static Permission Mod;
    public static Permission Admin;

    public static Permission Waystone;

    public static Permission Create;
    public static Permission CreateDiscoverable;
    public static Permission CreatePublic;
    public static Permission CreatePrivate;
    public static Permission CreateAdmin;
    public static Permission CreateBypass;
    public static Permission CreateLimit;

    public static Permission ChargeBypass;
    public static Permission Charge;

    public static Permission Nametag;
    public static Permission NametagOther;

    public static Permission DestroyOther;

    public static Permission List;
    public static Permission ListUnknown;
    public static Permission ListAll;

    public static Permission Teleport;
    public static Permission TeleportUnknown;
    public static Permission TeleportAll;

    public static Permission TeleportIgnoreWaystone;

    public static Permission Title;
    public static Permission TitleCreate;
    public static Permission TitleEnter;
    public static Permission TitleDiscover;

    public static void Setup(Server server)
    {
        Default = new Permission("waystone.default", "All of the normal commands to use this plugin.");
        Mod = new Permission("waystone.mod", "Allows for modification of other player's waystones");
        Admin = new Permission("waystone.admin", "Permission to create and break other player's waystones");
        Default.addParent(Mod, true);
        Mod.addParent(Admin, true);

        Waystone = new Permission("waystone.command", "Access to the Waystone command.");
        Waystone.addParent(Default, true);

        Create = new Permission("waystone.create", "Allows players to create new waystone.");
        Create.addParent(Default, true);
        CreateDiscoverable = new Permission("waystone.create.discoverable", "Allows players to create discoverable waystones.");
        Create.addParent(CreateDiscoverable, true);
        CreateDiscoverable.addParent(Default, true);
        CreatePublic = new Permission("waystone.create.public", "Allows players to create public waystones.");
        Create.addParent(CreatePublic, true);
        CreatePublic.addParent(Admin, true);
        CreatePrivate = new Permission("waystone.create.private", "Allows players to create private waystones.");
        Create.addParent(CreatePrivate, true);
        CreatePrivate.addParent(Default, true);
        CreateAdmin = new Permission("waystone.create.admin", "Allows players to create new admin owned waystone.");
        Create.addParent(CreateAdmin, true);
        CreateAdmin.addParent(Admin, true);
        CreateBypass = new Permission("waystone.create.limit.ignore", "Allows players to bypass waystone creation limit.");
        CreateLimit = new Permission("waystone.create.limit.0", "Limits a player to 0 waystones. Can increase 0 for higer limit.");

        Charge = new Permission("waystone.charge.0", "Time player has to wait to use the waystone after creation. Replace 0 with any higher value (in seconds)");
        ChargeBypass = new Permission("waystone.charge.ignore", "Ignores the charge time for waystones.");

        Nametag = new Permission("waystone.nametag", "Allows players to toggle nametags for their waystones.");
        Nametag.addParent(Default, true);
        NametagOther= new Permission("waystone.nametag.other", "Allows players to toggle nametags for other people's waystones.");
        NametagOther.addParent(Mod,true);
        Nametag.addParent(NametagOther,true);

        DestroyOther = new Permission("waystone.destroy.other", "Allows player to destroy other's waystones.");
        DestroyOther.addParent(Admin, true);

        List = new Permission("waystone.list", "Allows a player to see all known and public waystones.");
        List.addParent(Default, true);
        ListUnknown = new Permission("waystone.list.unknown", "Allows a player to see all known, unknown, and public waystones.");
        List.addParent(ListUnknown, true);
        ListAll = new Permission("waystone.list.all", "Allows a player to see all waystones.");
        ListUnknown.addParent(ListAll, true);

        Teleport = new Permission("waystone.teleport", "Allows a player to teleport to all known and public waystones.");
        Teleport.addParent(Default, true);
        TeleportUnknown = new Permission("waystone.teleport.unknown", "Allows a player to teleport to all known, unknown, and public waystones.");
        Teleport.addParent(TeleportUnknown, true);
        TeleportAll = new Permission("waystone.teleport.all", "Allows a player to teleport to all waystones.");
        TeleportUnknown.addParent(TeleportAll, true);

        TeleportIgnoreWaystone = new Permission("waystone.teleport.ignorewaystone", "Allows a player to teleport without a nearby waystone.");
        Teleport.addParent(TeleportIgnoreWaystone, true);

        Title = new Permission("waystone.title", "Allow a player to see titles from waystones");
        Title.addParent(Default, true);
        TitleCreate = new Permission("waystone.title.create", "Allow a player to see titles from waystones on Create");
        TitleCreate.addParent(Default, true);
        Title.addParent(TitleCreate, true);
        TitleDiscover = new Permission("waystone.title.discover", "Allow a player to see titles from waystones on Discover");
        TitleDiscover.addParent(Default, true);
        Title.addParent(TitleDiscover, true);
        TitleEnter = new Permission("waystone.title.enter", "Allow a player to see titles from waystones on Enter");
        TitleEnter.addParent(Default, true);
        Title.addParent(TitleDiscover, true);


        // Recalc & Register Perms

        SetPerm(Charge, server);
        SetPerm(ChargeBypass, server);

        SetPerm(CreateBypass, server);
        SetPerm(CreateDiscoverable, server);
        SetPerm(CreatePrivate, server);
        SetPerm(CreatePublic, server);
        SetPerm(CreateAdmin, server);
        SetPerm(Create, server);

        SetPerm(NametagOther, server);
        SetPerm(Nametag, server);

        SetPerm(DestroyOther, server);

        SetPerm(ListAll, server);
        SetPerm(ListUnknown, server);
        SetPerm(List, server);

        SetPerm(TeleportIgnoreWaystone, server);

        SetPerm(TeleportAll, server);
        SetPerm(TeleportUnknown, server);
        SetPerm(Teleport, server);

        SetPerm(TitleCreate, server);
        SetPerm(TitleDiscover, server);
        SetPerm(TitleEnter, server);
        SetPerm(Title, server);

        SetPerm(Waystone, server);

        SetPerm(Default, server);
        SetPerm(Mod, server);
        SetPerm(Admin, server);
    }

    private static void SetPerm(Permission perm, Server server)
    {
        PluginManager pm = server.getPluginManager();
        Set<Permission> permissions = pm.getPermissions();

        if (!permissions.contains(perm)) {
            pm.addPermission(perm);
        }
        perm.recalculatePermissibles();
    }
}
