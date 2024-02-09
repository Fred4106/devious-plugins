package com.lucidplugins.scurriushelper.api.util;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.widgets.Prayers;

public class CombatUtils
{
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
