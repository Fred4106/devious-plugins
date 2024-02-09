package com.lucidplugins.lucidlevihelper.api.util;

import com.lucidplugins.lucidlevihelper.api.item.SlottedItem;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.items.Inventory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryUtils
{
    public static List<SlottedItem> getAll()
    {
        return Inventory.getAll().stream().map(item -> new SlottedItem(item.getId(), item.getQuantity(), item.getSlot())).collect(Collectors.toList());
    }

    public static List<SlottedItem> getAll(Predicate<SlottedItem> filter)
    {
        return Inventory.getAll().stream().map(item -> new SlottedItem(item.getId(), item.getQuantity(), item.getSlot())).filter(filter).collect(Collectors.toList());
    }

    public static boolean contains(String itemName)
    {
        return Inventory.contains(itemName);
    }

    public static int calculateWidgetId(Client client, Item item)
    {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null)
        {
            return -1;
        }
        else
        {
            Widget[] children = inventoryWidget.getChildren();
            return children == null ? -1 : (Integer) Arrays.stream(children).filter((x) -> {
                return x.getItemId() == item.getId();
            }).findFirst().map(Widget::getId).orElse(-1);
        }
    }

    public static int getFreeSlots()
    {
        return Inventory.getFreeSlots();
    }

    public static boolean itemHasAction(Client client, int itemId, String action)
    {
        return Arrays.stream(client.getItemDefinition(itemId).getInventoryActions()).anyMatch(a -> a != null && a.equalsIgnoreCase(action));
    }

    public static void itemInteract(int itemId, String action)
    {
        final Item toInteract = Inventory.getFirst(itemId);
        if (toInteract != null)
        {
            toInteract.interact(action);
        }
    }

    public static void interactSlot(int slot, String action)
    {
        Item inSlot = Inventory.getItem(slot);

        if (inSlot != null)
        {
            inSlot.interact(action);
        }
    }


    public static Item getFirstItem(String name)
    {
        return Inventory.getFirst(item -> item.getName().toLowerCase().contains(name.toLowerCase()));
    }

    public static int count(String name)
    {
        List<Item> itemsToCount = Inventory.getAll(item -> item.getName().toLowerCase().contains(name.toLowerCase()));
        int count = 0;
        for (Item i : itemsToCount)
        {
            if (i != null)
            {
                count += i.getQuantity();
            }
        }

        return count;
    }

    public static int count(int id)
    {
        List<Item> itemsToCount = Inventory.getAll(item -> item.getId() == id);
        int count = 0;
        for (Item i : itemsToCount)
        {
            if (i != null)
            {
                count += i.getQuantity();
            }
        }
        return count;
    }
}
