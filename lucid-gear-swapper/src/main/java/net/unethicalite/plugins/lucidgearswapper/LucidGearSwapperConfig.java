package net.unethicalite.plugins.lucidgearswapper;

import net.runelite.client.config.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("lucid-gear-swapper")
public interface LucidGearSwapperConfig extends Config
{
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0,
            keyName = "generalSection"
    )
    String generalSection = "General";
    @ConfigSection(
            name = "Custom Swap 1",
            description = "Gear Swap # 1",
            position = 1,
            keyName = "swap1Section"
    )
    String swap1Section = "Custom Swap 1";
    @ConfigSection(
            name = "Custom Swap 2",
            description = "Gear Swap # 2",
            position = 2,
            keyName = "swap2Section"
    )
    String swap2Section = "Custom Swap 2";
    @ConfigSection(
            name = "Custom Swap 3",
            description = "Gear Swap # 3",
            position = 3,
            keyName = "swap3Section"
    )
    String swap3Section = "Custom Swap 3";
    @ConfigSection(
            name = "Custom Swap 4",
            description = "Gear Swap # 4",
            position = 4,
            keyName = "swap3Section"
    )
    String swap4Section = "Custom Swap 4";
    @ConfigSection(
            name = "Custom Swap 5",
            description = "Gear Swap # 5",
            position = 5,
            keyName = "swap5Section"
    )
    String swap5Section = "Custom Swap 5";
    @ConfigSection(
            name = "Custom Swap 6",
            description = "Gear Swap # 6",
            position = 6,
            keyName = "swap6Section"
    )
    String swap6Section = "Custom Swap 6";
    @ConfigItem(
            name = "1 tick swap",
            description = "Swaps in 1 tick if enabled and 2 if disabled",
            position = 0,
            keyName = "oneTickSwap",
            section = generalSection
    )
    default boolean oneTickSwap()
    {
        return true;
    }
    @ConfigItem(
            name = "Gear swap 1",
            description = "Names or IDs of your gear swap. Names match any item containing the name, meaning "
                    + "'Dharok's platelegs' matches all degradation values. Separate by line, semicolon or "
                    + "comma.",
            position = 0,
            keyName = "swap1String",
            section = swap1Section
    )
    default String swap1String()
    {
        return "";
    }
    @ConfigItem(
            name = "Gear swap 1 Hotkey",
            description = "Hotkey for gear swap 1",
            position = 1,
            keyName = "swap1Hotkey",
            section = swap1Section
    )
    default Keybind swap1Hotkey()
    {
        return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    }
    @ConfigItem(
            name = "Gear swap 2",
            description = "Names or IDs of your gear swap. Names match any item containing the name, meaning "
                    + "'Dharok's platelegs' matches all degradation values. Separate by line, semicolon or "
                    + "comma.",
            position = 0,
            keyName = "swap2String",
            section = swap2Section
    )
    default String swap2String()
    {
        return "";
    }
    @ConfigItem(
            name = "Gear swap 2 Hotkey",
            description = "Hotkey for gear swap 2",
            position = 1,
            keyName = "swap2Hotkey",
            section = swap2Section
    )
    default Keybind swap2Hotkey()
    {
        return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    }
    @ConfigItem(
            name = "Gear swap 3",
            description = "Names or IDs of your gear swap. Names match any item containing the name, meaning "
                    + "'Dharok's platelegs' matches all degradation values. Separate by line, semicolon or "
                    + "comma.",
            position = 0,
            keyName = "swap3String",
            section = swap3Section
    )
    default String swap3String()
    {
        return "";
    }
    @ConfigItem(
            name = "Gear swap 3 Hotkey",
            description = "Hotkey for gear swap 3",
            position = 1,
            keyName = "swap3Hotkey",
            section = swap3Section
    )
    default Keybind swap3Hotkey()
    {
        return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    }
    @ConfigItem(
            name = "Gear swap 4",
            description = "Names or IDs of your gear swap. Names match any item containing the name, meaning "
                    + "'Dharok's platelegs' matches all degradation values. Separate by line, semicolon or "
                    + "comma.",
            position = 0,
            keyName = "swap4String",
            section = swap4Section
    )
    default String swap4String()
    {
        return "";
    }
    @ConfigItem(
            name = "Gear swap 4 Hotkey",
            description = "Hotkey for gear swap 4",
            position = 1,
            keyName = "swap4Hotkey",
            section = swap4Section
    )
    default Keybind swap4Hotkey()
    {
        return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    }
    @ConfigItem(
            name = "Gear swap 5",
            description = "Names or IDs of your gear swap. Names match any item containing the name, meaning "
                    + "'Dharok's platelegs' matches all degradation values. Separate by line, semicolon or "
                    + "comma.",
            position = 0,
            keyName = "swap5String",
            section = swap5Section
    )
    default String swap5String()
    {
        return "";
    }
    @ConfigItem(
            name = "Gear swap 5 Hotkey",
            description = "Hotkey for gear swap 5",
            position = 1,
            keyName = "swap5Hotkey",
            section = swap5Section
    )
    default Keybind swap5Hotkey()
    {
        return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    }
    @ConfigItem(
            name = "Gear swap 6",
            description = "Names or IDs of your gear swap. Names match any item containing the name, meaning "
                    + "'Dharok's platelegs' matches all degradation values. Separate by line, semicolon or "
                    + "comma.",
            position = 0,
            keyName = "swap6String",
            section = swap6Section
    )
    default String swap6String()
    {
        return "";
    }
    @ConfigItem(
            name = "Gear swap 6 Hotkey",
            description = "Hotkey for gear swap 6",
            position = 1,
            keyName = "swap6Hotkey",
            section = swap6Section
    )
    default Keybind swap6Hotkey()
    {
        return new Keybind(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
    }
}
