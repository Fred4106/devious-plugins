package com.lucidplugins.lucidcustomprayers;

import lombok.Getter;
import net.runelite.api.Prayer;

public class ExportableConfig
{
    @Getter
    private boolean[] prayerEnabled;

    @Getter
    private String[] prayerIds;

    @Getter
    private String[] prayerDelays;

    @Getter
    private Prayer[] prayChoice;

    @Getter
    private EventType[] eventType;

    @Getter
    private boolean[] toggle;


    public ExportableConfig()
    {
        this.prayerEnabled = new boolean[6];
        this.prayerIds = new String[6];
        this.prayerDelays = new String[6];
        this.prayChoice = new Prayer[6];
        this.eventType = new EventType[6];
        this.toggle = new boolean[6];
    }

    public void setPrayer(int index, final boolean prayerEnabled, final String prayerIds, final String prayerDelays, final Prayer prayChoice,
                           final EventType eventType1, final boolean toggle1)
    {
        this.prayerEnabled[index] = prayerEnabled;
        this.prayerIds[index] = prayerIds;
        this.prayerDelays[index] = prayerDelays;
        this.prayChoice[index] = prayChoice;
        this.eventType[index] = eventType1;
        this.toggle[index] = toggle1;
    }
}
