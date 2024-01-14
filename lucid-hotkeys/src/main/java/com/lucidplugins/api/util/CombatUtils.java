package com.lucidplugins.api.util;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.widgets.Prayers;

public class CombatUtils
{
    public static Prayer prayerForName(String name)
    {
        String p = name.toUpperCase().replaceAll(" ", "_");
        for (Prayer prayer : Prayer.values())
        {
            if (prayer.name().equals(p))
            {
                return prayer;
            }
        }
        return null;
    }

    public static void togglePrayer(Client client, Prayer prayer)
    {
        if (client == null || (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && !client.isPrayerActive(prayer)))
        {
            return;
        }

        Prayers.toggle(prayer);
    }

    public static void activatePrayer(Client client, Prayer prayer)
    {
        if (client == null || client.getBoostedSkillLevel(Skill.PRAYER) == 0 || client.isPrayerActive(prayer))
        {
            return;
        }

        Prayers.toggle(prayer);
    }

    public static int getSpecEnergy(Client client)
    {
        return Vars.getVarp(300) / 10;
    }

    public static void toggleSpec()
    {
        Combat.toggleSpec();
    }
}
