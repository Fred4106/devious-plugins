package com.lucidplugins.api.util;

import net.runelite.api.*;
import net.unethicalite.api.entities.TileObjects;

import java.util.Arrays;

public class GameObjectUtils
{
    public static void interact(TileObject object, String action)
    {
        if (object.hasAction(action))
        {
            object.interact(action);
        }
    }

    public static boolean hasAction(Client client, int objectId, String action)
    {
        if (client == null)
        {
            return false;
        }

        ObjectComposition composition = client.getObjectDefinition(objectId);

        if (composition == null)
        {
            return false;
        }

        return Arrays.stream(composition.getActions()).anyMatch(s -> s != null && s.equalsIgnoreCase(action));
    }

    public static TileObject nearest(String name)
    {
        return TileObjects.getNearest(name);
    }

    public static TileObject nearest(int id)
    {
        return TileObjects.getNearest(id);
    }
}
