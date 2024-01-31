package com.lucidplugins.api.util;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
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

    public static void toggleQuickPrayers(Client client)
    {
        if (client == null || (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && !Prayers.isQuickPrayerEnabled()))
        {
            return;
        }

        Prayers.toggleQuickPrayer(!Prayers.isQuickPrayerEnabled());
    }

    public static void activateQuickPrayers(Client client)
    {
        if (client == null || (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && !Prayers.isQuickPrayerEnabled()))
        {
            return;
        }

        if (!Prayers.isQuickPrayerEnabled())
        {
            Prayers.toggleQuickPrayer(true);
        }
    }

    public static boolean isQuickPrayersEnabled(Client client)
    {
        return client.getVarbitValue(Varbits.QUICK_PRAYER) == 1;
    }

}
