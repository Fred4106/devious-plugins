package com.lucidplugins.api.util;

import net.runelite.api.*;
import net.unethicalite.api.entities.TileObjects;

import java.util.Arrays;

public class GameObjectUtils
{
    public static TileObject getFirstTileObjectAt(Tile tile, int... ids)
    {
        return TileObjects.getFirstAt(tile, ids);
    }

    public static void interact(GameObject object, String action)
    {
        object.interact(action);
    }

    public static void interact(WallObject object, String action)
    {
        object.interact(action);
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
}
