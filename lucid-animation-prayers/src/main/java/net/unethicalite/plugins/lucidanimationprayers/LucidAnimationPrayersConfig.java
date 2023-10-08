package net.unethicalite.plugins.lucidanimationprayers;

import net.runelite.api.Prayer;
import net.runelite.client.config.*;

@ConfigGroup("lucid-animation-prayers")
public interface LucidAnimationPrayersConfig extends Config
{
    @ConfigSection(
            name = "Custom Prayer 1",
            description = "Custom Prayer 1",
            position = 1,
            keyName = "prayer1Section"
    )
    String prayer1Section = "Custom Prayer 1";
    @ConfigSection(
            name = "Custom Prayer 2",
            description = "Custom Prayer 2",
            position = 2,
            keyName = "prayer2Section"
    )
    String prayer2Section = "Custom Prayer 2";
    @ConfigSection(
            name = "Custom Prayer 3",
            description = "Custom Prayer 3",
            position = 3,
            keyName = "prayer3Section"
    )
    String prayer3Section = "Custom Prayer 3";
    @ConfigSection(
            name = "Custom Prayer 4",
            description = "Custom Prayer 4",
            position = 4,
            keyName = "prayer4Section"
    )
    String prayer4Section = "Custom Prayer 4";
    @ConfigSection(
            name = "Custom Prayer 5",
            description = "Custom Prayer 5",
            position = 5,
            keyName = "prayer5Section"
    )
    String prayer5Section = "Custom Prayer 5";
    @ConfigSection(
            name = "Custom Prayer 6",
            description = "Custom Prayer 6",
            position = 6,
            keyName = "prayer6Section"
    )
    String prayer6Section = "Custom Prayer 6";
    @ConfigItem(
            name = "Animation IDs",
            description = "Enter animation IDs that will trigger this prayer, separated by commas",
            position = 0,
            keyName = "pray1Ids",
            section = prayer1Section
    )
    default String pray1Ids()
    {
        return "";
    }
    @ConfigItem(
            name = "Tick delays",
            description = "How many ticks after the animation plays to activate the prayer, separate multiple values by comma."
            + "One delay per ID required or leaving this completely blank will default to instant activation.",
            position = 1,
            keyName = "pray1delays",
            section = prayer1Section
    )
    default String pray1delays()
    {
        return "";
    }
    @ConfigItem(
            name = "Prayer to activate",
            description = "Which prayer will be activated?",
            position = 2,
            keyName = "pray1choice",
            section = prayer1Section
    )
    default Prayer pray1choice()
    {
        return Prayer.PIETY;
    }

    @ConfigItem(
            name = "Animation IDs",
            description = "Enter animation IDs that will trigger this prayer, separated by commas",
            position = 0,
            keyName = "pray2Ids",
            section = prayer2Section
    )
    default String pray2Ids()
    {
        return "";
    }
    @ConfigItem(
            name = "Tick delays",
            description = "How many ticks after the animation plays to activate the prayer, separate multiple values by comma."
                    + "One delay per ID required or leaving this completely blank will default to instant activation.",
            position = 1,
            keyName = "pray2delays",
            section = prayer2Section
    )
    default String pray2delays()
    {
        return "";
    }
    @ConfigItem(
            name = "Prayer to activate",
            description = "Which prayer will be activated?",
            position = 2,
            keyName = "pray2choice",
            section = prayer2Section
    )
    default Prayer pray2choice()
    {
        return Prayer.PIETY;
    }

    @ConfigItem(
            name = "Animation IDs",
            description = "Enter animation IDs that will trigger this prayer, separated by commas",
            position = 0,
            keyName = "pray3Ids",
            section = prayer3Section
    )
    default String pray3Ids()
    {
        return "";
    }
    @ConfigItem(
            name = "Tick delays",
            description = "How many ticks after the animation plays to activate the prayer, separate multiple values by comma."
                    + "One delay per ID required or leaving this completely blank will default to instant activation.",
            position = 1,
            keyName = "pray3delays",
            section = prayer3Section
    )
    default String pray3delays()
    {
        return "";
    }
    @ConfigItem(
            name = "Prayer to activate",
            description = "Which prayer will be activated?",
            position = 2,
            keyName = "pray3choice",
            section = prayer3Section
    )
    default Prayer pray3choice()
    {
        return Prayer.PIETY;
    }

    @ConfigItem(
            name = "Animation IDs",
            description = "Enter animation IDs that will trigger this prayer, separated by commas",
            position = 0,
            keyName = "pray4Ids",
            section = prayer4Section
    )
    default String pray4Ids()
    {
        return "";
    }
    @ConfigItem(
            name = "Tick delays",
            description = "How many ticks after the animation plays to activate the prayer, separate multiple values by comma."
                    + "One delay per ID required or leaving this completely blank will default to instant activation.",
            position = 1,
            keyName = "pray4delays",
            section = prayer4Section
    )
    default String pray4delays()
    {
        return "";
    }
    @ConfigItem(
            name = "Prayer to activate",
            description = "Which prayer will be activated?",
            position = 2,
            keyName = "pray4choice",
            section = prayer4Section
    )
    default Prayer pray4choice()
    {
        return Prayer.PIETY;
    }

    @ConfigItem(
            name = "Animation IDs",
            description = "Enter animation IDs that will trigger this prayer, separated by commas",
            position = 0,
            keyName = "pray5Ids",
            section = prayer5Section
    )
    default String pray5Ids()
    {
        return "";
    }
    @ConfigItem(
            name = "Tick delays",
            description = "How many ticks after the animation plays to activate the prayer, separate multiple values by comma."
                    + "One delay per ID required or leaving this completely blank will default to instant activation.",
            position = 1,
            keyName = "pray5delays",
            section = prayer5Section
    )
    default String pray5delays()
    {
        return "";
    }
    @ConfigItem(
            name = "Prayer to activate",
            description = "Which prayer will be activated?",
            position = 2,
            keyName = "pray5choice",
            section = prayer5Section
    )
    default Prayer pray5choice()
    {
        return Prayer.PIETY;
    }

    @ConfigItem(
            name = "Animation IDs",
            description = "Enter animation IDs that will trigger this prayer, separated by commas",
            position = 0,
            keyName = "pray6Ids",
            section = prayer6Section
    )
    default String pray6Ids()
    {
        return "";
    }
    @ConfigItem(
            name = "Tick delays",
            description = "How many ticks after the animation plays to activate the prayer, separate multiple values by comma."
                    + "One delay per ID required or leaving this completely blank will default to instant activation.",
            position = 1,
            keyName = "pray6delays",
            section = prayer6Section
    )
    default String pray6delays()
    {
        return "";
    }
    @ConfigItem(
            name = "Prayer to activate",
            description = "Which prayer will be activated?",
            position = 2,
            keyName = "pray6choice",
            section = prayer6Section
    )
    default Prayer pray6choice()
    {
        return Prayer.PIETY;
    }
}
