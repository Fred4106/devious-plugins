package com.lucidplugins.api.util;

import net.runelite.api.NPC;
import net.unethicalite.api.entities.NPCs;

public class NpcUtils
{

    public static NPC getNearest(String name)
    {
        return NPCs.getNearest(name);
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
