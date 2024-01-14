package com.lucidplugins.lucidhotkeys;

import lombok.Getter;
import net.runelite.client.config.Keybind;

public class ExportableConfig
{
    @Getter
    public Keybind[] hotkey;

    @Getter
    public String[] actions;

    @Getter
    public String[] preconditions;

    public ExportableConfig()
    {
        hotkey = new Keybind[15];
        actions = new String[15];
        preconditions = new String[15];
    }

    public void setHotkeyConfig(int index, Keybind hotkey, String actions, String preconditions)
    {
        this.hotkey[index] = hotkey;
        this.actions[index] = actions;
        this.preconditions[index] = preconditions;
    }
}
