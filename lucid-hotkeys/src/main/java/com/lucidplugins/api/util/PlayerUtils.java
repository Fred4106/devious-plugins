package com.lucidplugins.api.util;

import net.runelite.api.Player;
import net.unethicalite.api.entities.Players;

public class PlayerUtils
{
    public static void interactPlayer(String name, String action)
    {
        Players.getNearest(name).interact(action);
    }

    public static Player getNearest(String name)
    {
        return Players.getNearest(player -> player.getName().contains(name));
    }

}
