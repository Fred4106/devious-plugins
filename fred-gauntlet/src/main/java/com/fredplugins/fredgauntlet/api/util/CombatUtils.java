package com.fredplugins.fredgauntlet.api.util;

import net.runelite.api.Client;
import net.runelite.api.Item;
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

    public static void deactivatePrayers(boolean protectionOnly)
    {
        if (!Prayers.anyActive())
        {
            return;
        }

        if (protectionOnly)
        {
            if (Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
            {
                Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
            }

            if (Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC))
            {
                Prayers.toggle(Prayer.PROTECT_FROM_MAGIC);
            }

            if (Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
            {
                Prayers.toggle(Prayer.PROTECT_FROM_MELEE);
            }
        }
        else
        {
            Prayers.disableAll();
        }
    }

    public static int getSpecEnergy(Client client)
    {
        return Vars.getVarp(300) / 10;
    }

    public static void quickKeris(Client client)
    {
        if (client == null)
        {
            return;
        }

        Item keris = InventoryUtils.getFirstItem("Keris partisan of the sun");
        boolean kerisEquipped = EquipmentUtils.getWepSlotItem().getName().contains("of the sun");
        if ((keris == null && !kerisEquipped) || getSpecEnergy(client) < 75)
        {
            return;
        }

        if (keris != null)
        {
            keris.interact("Wield");
        }

        Combat.toggleSpec();

    }
}
