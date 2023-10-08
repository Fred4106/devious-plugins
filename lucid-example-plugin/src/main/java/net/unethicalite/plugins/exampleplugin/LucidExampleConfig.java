package net.unethicalite.plugins.exampleplugin;

import net.runelite.client.config.*;

@ConfigGroup("lucid-example")
public interface LucidExampleConfig extends Config
{
    @ConfigSection(
            name = "Section 1",
            description = "Section 1 description",
            position = 0,
            keyName = "section1"
    )
    String section1 = "Section 1";
    @ConfigItem(
            name = "Toggle 1",
            description = "A boolean toggle",
            position = 0,
            keyName = "toggle1",
            section = section1
    )
    default boolean toggle1()
    {
        return false;
    }
}
