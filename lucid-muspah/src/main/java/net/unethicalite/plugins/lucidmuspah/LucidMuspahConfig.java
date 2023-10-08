package net.unethicalite.plugins.lucidmuspah;

import net.runelite.client.config.*;

@ConfigGroup("lucid-gear-swapper")
public interface LucidMuspahConfig extends Config
{
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0,
            keyName = "generalSection"
    )
    String generalSection = "General";
    @ConfigItem(
            name = "Auto-pray",
            description = "Swaps prayers when muspah changes forms + does magic attack",
            position = 0,
            keyName = "autoPray",
            section = generalSection
    )
    default boolean autoPray()
    {
        return false;
    }
    @ConfigItem(
            name = "Melee step-back tick overlay",
            description = "Overlays ticks until muspah does melee attack for step-back method",
            position = 1,
            keyName = "stepBackOverlay",
            section = generalSection
    )
    default boolean stepBackOverlay()
    {
        return false;
    }
    @ConfigItem(
            name = "Auto melee step-back",
            description = "Will step back 1 tile 1 tick before you get hit by the melee attack",
            position = 2,
            keyName = "autoStepBack",
            section = generalSection
    )
    default boolean autoStepBack()
    {
        return false;
    }
}
