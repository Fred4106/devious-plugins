package com.lucidplugins.lucidcannonreloader.api.util;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.WallObject;

import java.util.Arrays;

public class GameObjectUtils
{
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
