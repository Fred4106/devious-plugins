package com.lucidplugins.lucidcombat.api.util;

import com.lucidplugins.lucidcombat.api.item.SlottedItem;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.unethicalite.api.items.Equipment;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EquipmentUtils
{
    public static Item getWepSlotItem()
    {
        return Equipment.fromSlot(EquipmentInventorySlot.WEAPON);
    }

    public static boolean contains(int... ids)
    {
        return Equipment.contains(ids);
    }

    public static boolean contains(String name)
    {
        return Equipment.contains(item -> item.getName().contains(name));
    }
    public static List<SlottedItem> getAll()
    {
        return Equipment.getAll().stream().map(item -> new SlottedItem(item.getId(), item.getQuantity(), item.getSlot())).collect(Collectors.toList());
    }

    public static List<SlottedItem> getAll(Predicate<SlottedItem> filter)
    {
        return Equipment.getAll().stream().map(item -> new SlottedItem(item.getId(), item.getQuantity(), item.getSlot())).filter(filter).collect(Collectors.toList());
    }
}
