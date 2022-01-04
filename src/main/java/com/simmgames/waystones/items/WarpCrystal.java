package com.simmgames.waystones.items;

import com.simmgames.waystones.data.Config;
import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.util.Default;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WarpCrystal {
    static Plugin plugin;
    static NamespacedKey uses;
    static String crystalValue;

    public static void Setup(Plugin plugin)
    {
        WarpCrystal.plugin = plugin;
        WarpCrystal.uses = new NamespacedKey(plugin, "uses");
        WarpCrystal.crystalValue = "warp_crystal";
    }
    public static void AddRecipe()
    {
        NamespacedKey nsk = new NamespacedKey(plugin, crystalValue);
        ShapedRecipe recipe = new ShapedRecipe(nsk, GetItem());

        recipe.shape("eie","bsb", "eie");

        recipe.setIngredient('e', Material.ENDER_EYE);
        recipe.setIngredient('i', Material.IRON_INGOT);
        recipe.setIngredient('s', Material.AMETHYST_SHARD);
        recipe.setIngredient('b', Material.BLAZE_POWDER);

        plugin.getServer().addRecipe(recipe);
    }
    public static ItemStack GetItem()
    {
        ItemStack Crystal = new ItemStack(Material.AMETHYST_SHARD);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Unlinked");
        lore.add(ChatColor.WHITE + "Used 0 Times");

        ItemMeta metadata = Crystal.getItemMeta();
        metadata.getPersistentDataContainer().set(WarpItem.customItem, PersistentDataType.STRING, crystalValue);
        metadata.getPersistentDataContainer().set(WarpItem.waystone, PersistentDataType.STRING, Default.UUIDZero);
        metadata.getPersistentDataContainer().set(uses, PersistentDataType.INTEGER, 0);
        metadata.setDisplayName(ChatColor.LIGHT_PURPLE + "Warp Crystal");
        metadata.setLore(lore);
        Crystal.setItemMeta(metadata);
        return Crystal;
    }
    public static boolean IsWarpCrystal(ItemStack Item)
    {
        if(Item.getType() != Material.AMETHYST_SHARD)
            return false;
        ItemMeta metadata = Item.getItemMeta();
        PersistentDataContainer data = metadata.getPersistentDataContainer();
        if(data.has(WarpItem.customItem, PersistentDataType.STRING))
        {
            String value = data.get(WarpItem.customItem, PersistentDataType.STRING);
            if(value.equalsIgnoreCase(crystalValue))
                return true;
        }
        return false;
    }
    public static int NumOfUses(ItemStack warpCrystal)
    {
        if(!IsWarpCrystal(warpCrystal))
            return -1;
        ItemMeta metadata = warpCrystal.getItemMeta();
        PersistentDataContainer data = metadata.getPersistentDataContainer();
        if(data.has(uses, PersistentDataType.INTEGER))
        {
            return data.get(uses, PersistentDataType.INTEGER);
        }
        return -1;
    }
    public static void SetUses(ItemStack warpCrystal, int value)
    {
        if(!IsWarpCrystal(warpCrystal))
            return;
        ItemMeta metadata = warpCrystal.getItemMeta();
        PersistentDataContainer data = metadata.getPersistentDataContainer();
        data.set(uses, PersistentDataType.INTEGER, value);
        warpCrystal.setItemMeta(metadata);
        return;
    }
    public static void Used(ItemStack warpCrystal)
    {
        if(!IsWarpCrystal(warpCrystal))
            return;
        SetUses(warpCrystal, NumOfUses(warpCrystal) + 1);
        return;
    }
    public static String ExtraLore(ItemStack item)
    {
        return ChatColor.WHITE + "Used " + NumOfUses(item) + " Times";
    }
    public static double GetChanceRate(ItemStack item)
    {
        return ((double)NumOfUses(item) / Config.WarpCrystalMax()) * Config.WarpCrystalPercent();
    }
}
