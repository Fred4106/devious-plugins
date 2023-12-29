package com.lucidplugins.lucidmuspah;

import com.google.inject.Provides;
import com.lucidplugins.api.util.CombatUtils;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.queries.NPCQuery;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import com.lucidplugins.lucidmuspah.overlay.OverlayMuspah;
import org.pf4j.Extension;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "Lucid Muspah",
        description = "Helper plugin for the Phantom Muspah (still in development)",
        enabledByDefault = false,
        tags = {"muspah"}
)
public class LucidMuspahPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidMuspahConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private OverlayMuspah overlayMuspah;

    private static final int MUSPAH_MELEE_ANIM = 9920;

    private static final int MUSPAH_MAGIC_SPECIAL_ANIM = 9918;

    @Getter
    private int ticksUntilAttack = -1;

    private int lastUpdateTick = -1;

    private List<ScheduledPrayer> scheduledPrayers = new ArrayList<>();

    @Provides
    LucidMuspahConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidMuspahConfig.class);
    }

    private void pluginEnabled()
    {
        if (!overlayManager.anyMatch(p -> p == overlayMuspah))
        {
            overlayManager.add(overlayMuspah);
        }
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    @Override
    protected void shutDown()
    {
        if (overlayManager.anyMatch(p -> p == overlayMuspah))
        {
            overlayManager.remove(overlayMuspah);
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (lastUpdateTick != client.getTickCount())
        {
            lastUpdateTick = client.getTickCount();

            if (ticksUntilAttack > 0)
            {
                ticksUntilAttack--;
            }

            for (ScheduledPrayer prayer : scheduledPrayers)
            {
                if (client.getTickCount() == prayer.getActivationTick())
                {
                    CombatUtils.activatePrayer(client, prayer.getPrayer());
                }
            }

            scheduledPrayers.removeIf(prayer -> prayer.getActivationTick() <= client.getTickCount() - 1);
        }
    }

    @Subscribe
    private void onNpcChanged(final NpcChanged event)
    {
        if (event.getNpc().getId() == NpcID.PHANTOM_MUSPAH_12078)
        {
            ticksUntilAttack = 5;

            if (config.autoPray())
            {
                scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MELEE, client.getTickCount() + 2));
            }
        }

        if (event.getNpc().getId() == NpcID.PHANTOM_MUSPAH_12079)
        {
            if (config.autoPray())
            {
                scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MISSILES, client.getTickCount() + 2));
            }
        }

        if (event.getNpc().getId() == NpcID.PHANTOM_MUSPAH)
        {
            if (config.autoPray())
            {
                scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MISSILES, client.getTickCount() + 2));
            }
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        if (event.getNpc().getId() == NpcID.PHANTOM_MUSPAH_12078)
        {
            ticksUntilAttack = 5;

            if (config.autoPray())
            {
                scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MELEE, client.getTickCount() + 2));
            }
        }

        if (event.getNpc().getId() == NpcID.PHANTOM_MUSPAH)
        {
            if (config.autoPray())
            {
                scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MISSILES, client.getTickCount() + 2));
            }
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        int animId = event.getActor().getAnimation();

        if (animId == MUSPAH_MELEE_ANIM)
        {
            ticksUntilAttack = 5;
        }

        if (animId == MUSPAH_MAGIC_SPECIAL_ANIM)
        {
            scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MAGIC, client.getTickCount() + 2));
            scheduledPrayers.add(new ScheduledPrayer(Prayer.PROTECT_FROM_MISSILES, client.getTickCount() + 4));
        }
    }

    public boolean inMeleeForm()
    {
        return new NPCQuery().idEquals(NpcID.PHANTOM_MUSPAH_12078).result(client).nearestTo(client.getLocalPlayer()) != null;
    }
}