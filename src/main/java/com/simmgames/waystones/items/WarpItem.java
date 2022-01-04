package com.simmgames.waystones.items;


import com.simmgames.waystones.data.Waystone;
import com.simmgames.waystones.util.Default;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WarpItem {
    static Plugin _plugin;
    public static NamespacedKey customItem;
    public static NamespacedKey waystone;
    public static NamespacedKey noStack;

    public String type;
    public String waystoneUUID;
    public Boolean isStackable;
    public ItemStack item;

    public static void Setup(Plugin plugin)
    {
        _plugin = plugin;
        customItem = new NamespacedKey(plugin, "waystones_item");
        waystone = new NamespacedKey(plugin, "waystone");
        noStack = new NamespacedKey(plugin, "no-stack");
    }

    public WarpItem(ItemStack item)
    {
        ItemMeta metadata = item.getItemMeta();
        PersistentDataContainer data = metadata.getPersistentDataContainer();
        if(data.has(customItem, PersistentDataType.STRING))
        {
            type = data.get(customItem, PersistentDataType.STRING);
        } else
        {
            type = "Unknown";
        }
        if(data.has(waystone, PersistentDataType.STRING))
        {
            waystoneUUID = data.get(waystone, PersistentDataType.STRING);
        }
        else
        {
            waystoneUUID = Default.UUIDZero;
        }
        if(data.has(noStack, PersistentDataType.STRING))
        {
            String randomUUID = data.get(noStack, PersistentDataType.STRING);
            if(randomUUID.equalsIgnoreCase(Default.UUIDZero))
                isStackable = true;
            else
                isStackable = false;
        }
        else
        {
            isStackable = true;
        }
        this.item = item;
    }

    public static boolean isWarpItem(ItemStack item)
    {
        WarpItem warpItem = new WarpItem(item);
        if(warpItem.type.equalsIgnoreCase("Unknown"))
            return false;
        return true;
    }

    public static String GetWaystoneUUID(ItemStack item)
    {
        return new WarpItem(item).waystoneUUID;
    }

    public ItemStack SetWaystone(String waystoneUUID, String waystoneName)
    {
        ItemStack linkedItem = item.clone();

        RemoveItem();
        linkedItem.setAmount(1);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Linked to Waystone");
        lore.add(ChatColor.AQUA + waystoneName);

        linkedItem.addUnsafeEnchantment(Enchantment.LURE, 1);

        ItemMeta metadata = linkedItem.getItemMeta();
        metadata.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        metadata.setLore(lore);
        metadata.getPersistentDataContainer().set(waystone, PersistentDataType.STRING, waystoneUUID);
        linkedItem.setItemMeta(metadata);

        return linkedItem;
    }

    public void AppendLore(String loreToAdd)
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add(loreToAdd);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static WarpItem AppendLore(ItemStack item, String loreToAdd)
    {
        WarpItem warpItem = new WarpItem(item);
        warpItem.AppendLore(loreToAdd);
        return warpItem;
    }
    public void ModifyLore(int position, String newLore)
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(position, newLore);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void RemoveItem()
    {
        int amount = item.getAmount();
        item.setAmount(amount-1);
    }
    public static WarpItem RemoveItem(ItemStack item)
    {
        WarpItem warpItem = new WarpItem(item);
        warpItem.RemoveItem();
        return warpItem;
    }
    public void RemoveItem(double onChance)
    {
        if(new Random().nextDouble() < onChance)
            RemoveItem();
    }
    public static WarpItem RemoveItem(ItemStack item, double onChance)
    {
        WarpItem warpItem = new WarpItem(item);
        warpItem.RemoveItem(onChance);
        return warpItem;
    }

    public void SetNotStackable()
    {
        ItemMeta metadata = item.getItemMeta();
        metadata.getPersistentDataContainer().set(WarpItem.noStack, PersistentDataType.STRING, UUID.randomUUID().toString());
        item.setItemMeta(metadata);
        isStackable = false;
    }

    public static WarpItem SetNotStackable(ItemStack item)
    {
        WarpItem warpItem = new WarpItem(item);
        warpItem.SetNotStackable();
        return warpItem;
    }

    public ItemStack Unlink()
    {
        ItemStack linkedItem = item.clone();

        RemoveItem();
        linkedItem.setAmount(1);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Unlinked");

        linkedItem.removeEnchantment(Enchantment.LURE);

        ItemMeta metadata = linkedItem.getItemMeta();
        metadata.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        metadata.setLore(lore);
        metadata.getPersistentDataContainer().set(waystone, PersistentDataType.STRING, Default.UUIDZero);
        if(!isStackable)
        {
            metadata.getPersistentDataContainer().remove(noStack);
            isStackable = true;
        }

        linkedItem.setItemMeta(metadata);
        return linkedItem;
    }
}
