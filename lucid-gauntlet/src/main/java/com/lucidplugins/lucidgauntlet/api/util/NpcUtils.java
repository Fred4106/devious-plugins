package com.lucidplugins.lucidgauntlet.api.util;

import net.runelite.api.NPC;
import net.unethicalite.api.entities.NPCs;

import java.util.List;
import java.util.function.Predicate;

public class NpcUtils
{
    public static NPC getNearestNpc(Predicate<NPC> filter)
    {
        return NPCs.getNearest(filter);
    }

    public static NPC getNearestNpc(String name)
    {
        return NPCs.getNearest(name);
    }

    public static List<NPC> getAllNpcs(int... ids)
    {
        return NPCs.getAll(ids);
    }

    public static void attackNpc(NPC npc)
    {
        if (npc == null)
        {
            return;
        }

        npc.interact("Attack");
    }
}
