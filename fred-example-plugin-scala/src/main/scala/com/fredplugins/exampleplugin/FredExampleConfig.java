package com.fredplugins.exampleplugin;

import net.runelite.client.config.*;

@ConfigGroup("fred-example")
public interface FredExampleConfig extends Config
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

    @ConfigItem(
            name = "Multi-option 1",
            description = "A multi-option combo box using an enum",
            position = 1,
            keyName = "multiOption1",
            section = section1
    )
    default Option multiOption1()
    {
        return Option.OPTION_1;
    }

    @ConfigItem(
            name = "String input",
            description = "A string input",
            position = 2,
            keyName = "stringInput",
            section = section1
    )
    default String string1()
    {
        return "";
    }

    @ConfigItem(
            name = "Integer option with range",
            description = "An integer option with a range between 1-100",
            position = 3,
            keyName = "integer1",
            section = section1
    )
    @Range(min = 1, max = 100)
    default int integer1()
    {
        return 1;
    }

    enum Option
    {
        OPTION_1, OPTION_2, OPTION_3
    }
}
