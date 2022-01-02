package com.simmgames.waystones.data;

import org.bukkit.ChatColor;

public class Local {
    public static String NoPermsCommand()
    {
        return ChatColor.DARK_RED + "You don't have permissions to run this command.";
    }
    public static String NoPerms()
    {
        return ChatColor.DARK_RED + "You don't have permissions to do this.";
    }
    public static String NotOwned()
    {
        return ChatColor.RED + "You do not own this Waystone.";
    }
}
