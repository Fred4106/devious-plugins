package com.lucidplugins.luciddukehelper.api.util;

import net.runelite.api.NPC;
import net.unethicalite.api.entities.NPCs;

import java.util.List;
import java.util.function.Predicate;

public class NpcUtils
{

    public static List<NPC> getAll(Predicate<NPC> filter)
    {
        return NPCs.getAll(filter);
    }

    public static NPC getNearest(String name)
    {
        return NPCs.getNearest(name);
    }

    public static NPC getNearest(Predicate<NPC> filter)
    {
        return NPCs.getNearest(filter);
    }

    public static NPC getNearest(int id)
    {
        return NPCs.getNearest(id);
    }

    public static void interact(NPC npc, String action)
    {
        if (npc.hasAction(action))
        {
            npc.interact(action);
        }
    }

}
