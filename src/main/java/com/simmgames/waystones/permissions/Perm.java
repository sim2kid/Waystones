package com.simmgames.waystones.permissions;

import org.bukkit.permissions.Permission;

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

    public static Permission Nametag;
    public static Permission NametagOther;

    public static Permission DestroyOther;

    public static Permission List;
    public static Permission ListUnknown;
    public static Permission ListAll;

    public static Permission Teleport;
    public static Permission TeleportUnknown;
    public static Permission TeleportAll;

    public static Permission Title;
    public static Permission TitleCreate;
    public static Permission TitleEnter;
    public static Permission TitleDiscover;

    public static void Setup()
    {
        Default = new Permission("waystone.default", "All of the normal commands to use this plugin.");
        Mod = new Permission("waystone.mod", "Allows for modification of other player's waystones");
        Mod.addParent(Default, true);
        Admin = new Permission("waystone.admin", "Permission to create and break other player's waystones");
        Admin.addParent(Mod, true);

        Waystone = new Permission("waystone.command", "Access to the Waystone command.");
        Waystone.addParent(Default, true);

        Create = new Permission("waystone.create", "Allows players to create new waystone.");
        Create.addParent(Default, true);
        CreateDiscoverable = new Permission("waystone.create.discoverable", "Allows players to create discoverable waystones.");
        CreateDiscoverable.addParent(Create, true);
        CreatePublic = new Permission("waystone.create.public", "Allows players to create public waystones.");
        CreatePublic.addParent(Admin, true);
        CreatePrivate = new Permission("waystone.create.private", "Allows players to create private waystones.");
        CreatePrivate.addParent(Create, true);
        CreateAdmin = new Permission("waystone.create.admin", "Allows players to create new admin owned waystone.");
        CreateAdmin.addParent(Admin, true);

        Nametag = new Permission("waystone.nametag", "Allows players to toggle nametags for their waystones.");
        Nametag.addParent(Default, true);
        NametagOther= new Permission("waystone.nametag.other", "Allows players to toggle nametags for other people's waystones.");
        NametagOther.addParent(Mod,true);

        DestroyOther = new Permission("waystone.destroy.other", "Allows player to destroy other's waystones.");
        DestroyOther.addParent(Admin, true);

        List = new Permission("waystone.list", "Allows a player to see all known and public waystones.");
        List.addParent(Default, true);
        ListUnknown = new Permission("waystone.list.unknown", "Allows a player to see all known, unknown, and public waystones.");
        ListUnknown.addParent(List, true);
        ListAll = new Permission("waystone.list.all", "Allows a player to see all waystones.");
        ListAll.addParent(ListUnknown, true);

        Teleport = new Permission("waystone.teleport", "Allows a player to teleport to all known and public waystones.");
        Teleport.addParent(Default, true);
        TeleportUnknown = new Permission("waystone.teleport.unknown", "Allows a player to teleport to all known, unknown, and public waystones.");
        TeleportUnknown.addParent(Teleport, true);
        TeleportAll = new Permission("waystone.teleport.all", "Allows a player to teleport to all waystones.");
        TeleportAll.addParent(TeleportUnknown, true);

        Title = new Permission("waystone.title", "Allow a player to see titles from waystones");
        Title.addParent(Default, true);
        TitleCreate = new Permission("waystone.title.create", "Allow a player to see titles from waystones on Create");
        TitleCreate.addParent(Default, true);
        TitleCreate.addParent(Title, true);
        TitleDiscover = new Permission("waystone.title.discover", "Allow a player to see titles from waystones on Discover");
        TitleDiscover.addParent(Default, true);
        TitleDiscover.addParent(Title, true);
        TitleEnter = new Permission("waystone.title.enter", "Allow a player to see titles from waystones on Enter");
        TitleEnter.addParent(Default, true);
        TitleEnter.addParent(Title, true);
    }
}
