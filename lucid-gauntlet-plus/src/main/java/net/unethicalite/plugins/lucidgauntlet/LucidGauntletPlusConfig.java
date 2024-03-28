package net.unethicalite.plugins.lucidgauntlet;

import net.runelite.client.config.*;

@ConfigGroup("lucid-gauntlet-plus")
public interface LucidGauntletPlusConfig extends Config
{
    @ConfigSection(
            keyName = "mainSection",
            name = "Main",
            description = "",
            position = 0
    )
    String mainSection = "Main";
    @ConfigSection(
            keyName = "prepSection",
            name = "Prep",
            description = "",
            position = 1
    )
    String prepSection = "Prep";
    @ConfigSection(
            keyName = "bosSection",
            name = "Boss",
            description = "",
            position = 2
    )
    String bossSection = "Boss";
    @ConfigSection(
            keyName = "prayerSection",
            name = "Prayer",
            description = "",
            position = 3
    )
    String prayerSection = "Prayer";
    @ConfigItem(
            keyName = "enterCorrupted",
            name = "Corrupted Gauntlet",
            description = "Chose whether to enter normal gauntlet or corrupted",
            position = 0,
            section = mainSection
    )
    default boolean enterCorrupted()
    {
        return false;
    }
    @ConfigItem(
            keyName = "oneTickFlick",
            name = "1 Tick Flick",
            description = "Chose whether to 1 tick flick to preserve prayer",
            position = 1,
            section = mainSection
    )
    default boolean oneTickFlick()
    {
        return false;
    }
    @ConfigItem(
            keyName = "useOffensivePrayer",
            name = "Use offensive prayers",
            description = "Chose whether to use offensive prayers or not. Will drastically raise prayer drain unless 1-tick flicking.",
            position = 2,
            section = mainSection
    )
    default boolean useOffensivePrayer()
    {
        return false;
    }
    @ConfigItem(
            keyName = "foodAmt",
            name = "Amount of Food",
            description = "Amount of food to gather and cook.",
            position = 0,
            section = prepSection
    )
    default int foodAmt()
    {
        return 16;
    }
    @ConfigItem(
            keyName = "potionAmt",
            name = "Amount of Egniols",
            description = "Amount of Egniols to make.",
            position = 1,
            section = prepSection
    )
    default int potionAmt()
    {
        return 1;
    }
    @ConfigItem(
            keyName = "healthMin",
            name = "Health to Eat",
            description = "Minimum health before eating (boss fight only)",
            position = 0,
            section = bossSection
    )
    default int healthMin()
    {
        return 30;
    }
    @ConfigItem(
            keyName = "healthMax",
            name = "Health to Stop At",
            description = "Stop eating past this level",
            position = 1,
            section = bossSection
    )
    default int healthMax()
    {
        return 80;
    }
    @ConfigItem(
            keyName = "prayMin",
            name = "Pray to Drink",
            description = "Minimum prayer before drinking pots (boss fight only)",
            position = 2,
            section = bossSection
    )
    default int prayMin()
    {
        return 40;
    }
    @ConfigItem(
            keyName = "runMin",
            name = "Run to Drink",
            description = "Minimum run energy before drinking pots (boss fight only)",
            position = 3,
            section = bossSection
    )
    default int runMin()
    {
        return 10;
    }
    @ConfigItem(
            keyName = "useRigour",
            name = "Rigour",
            description = "",
            position = 0,
            section = prayerSection
    )
    default boolean useRigour()
    {
        return false;
    }
    @ConfigItem(
            keyName = "useAugury",
            name = "Augury",
            description = "",
            position = 1,
            section = prayerSection
    )
    default boolean useAugury()
    {
        return false;
    }
    @ConfigItem(
            keyName = "usePiety",
            name = "Piety",
            description = "",
            position = 2,
            section = prayerSection
    )
    default boolean usePiety()
    {
        return false;
    }
}
