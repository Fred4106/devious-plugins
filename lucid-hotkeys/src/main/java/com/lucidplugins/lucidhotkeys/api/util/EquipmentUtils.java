package com.lucidplugins.lucidhotkeys.api.util;

import com.lucidplugins.lucidhotkeys.api.item.SlottedItem;
import net.unethicalite.api.items.Equipment;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EquipmentUtils
{
    public static List<SlottedItem> getAll()
    {
        return Equipment.getAll().stream().map(item -> new SlottedItem(item.getId(), item.getQuantity(), item.getSlot())).collect(Collectors.toList());
    }

    public static List<SlottedItem> getAll(Predicate<SlottedItem> filter)
    {
        return Equipment.getAll().stream().map(item -> new SlottedItem(item.getId(), item.getQuantity(), item.getSlot())).filter(filter).collect(Collectors.toList());
    }
}
