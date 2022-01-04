package com.simmgames.waystones.items;

import com.simmgames.waystones.util.Default;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
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

public class WarpScroll {
    static Plugin plugin;
    static String scrollValue;

    public static void Setup(Plugin plugin)
    {
        WarpScroll.plugin = plugin;
        WarpScroll.scrollValue = "warp_scroll";
    }
    public static void AddRecipe()
    {
        NamespacedKey nsk = new NamespacedKey(plugin, scrollValue);
        ShapedRecipe recipe = new ShapedRecipe(nsk, GetItem());

        recipe.shape("*n*","epe", "*n*");

        //recipe.setIngredient('*', Material.AIR);
        recipe.setIngredient('n', Material.IRON_NUGGET);
        recipe.setIngredient('p', Material.PAPER);
        recipe.setIngredient('e', Material.ENDER_EYE);

        plugin.getServer().addRecipe(recipe);
    }
    public static ItemStack GetItem()
    {
        ItemStack Scroll = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Unlinked");

        ItemMeta metadata = Scroll.getItemMeta();
        metadata.getPersistentDataContainer().set(WarpItem.customItem, PersistentDataType.STRING, scrollValue);
        metadata.getPersistentDataContainer().set(WarpItem.waystone, PersistentDataType.STRING, Default.UUIDZero);
        metadata.setDisplayName(ChatColor.LIGHT_PURPLE + "Warp Scroll");
        metadata.setLore(lore);
        Scroll.setItemMeta(metadata);
        return Scroll;
    }
    public static boolean IsWarpScroll(ItemStack Item)
    {
        if(Item.getType() != Material.PAPER)
            return false;
        ItemMeta metadata = Item.getItemMeta();
        PersistentDataContainer data = metadata.getPersistentDataContainer();
        if(data.has(WarpItem.customItem, PersistentDataType.STRING))
        {
            String value = data.get(WarpItem.customItem, PersistentDataType.STRING);
            if(value.equalsIgnoreCase(scrollValue))
                return true;
        }
        return false;
    }
    public static ItemStack OnUse(ItemStack item)
    {
        if(!IsWarpScroll(item))
            return item;

        // Destroy One Item
        int amount = item.getAmount();
        if(amount - 1 <= 0)
            return new ItemStack(Material.AIR);
        item.setAmount(amount-1);

        return item;
    }
}
