package net.unethicalite.plugins.lucidmuspah;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.plugins.lucidmuspah.overlay.OverlayMuspah;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;

@Extension
@PluginDescriptor(
        name = "Lucid Muspah",
        description = "Helper plugin for the Phantom Muspah (still in development)",
        enabledByDefault = false,
        tags = {"muspah"}
)
@Singleton
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
        }
    }

    @Subscribe
    private void onNpcChanged(final NpcChanged event)
    {
        if (event.getOld().getId() == NpcID.PHANTOM_MUSPAH && event.getNpc().getId() == NpcID.PHANTOM_MUSPAH_12078)
        {
            ticksUntilAttack = 5;
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        switch (event.getNpc().getId())
        {
            case NpcID.PHANTOM_MUSPAH:
            case NpcID.PHANTOM_MUSPAH_12078:
            case NpcID.PHANTOM_MUSPAH_12079:
            case NpcID.PHANTOM_MUSPAH_12080:
            case NpcID.PHANTOM_MUSPAH_12082:
                MessageUtils.addMessage("Npc: " + event.getNpc().getId() + " spawned named: " + event.getNpc().getName());
                break;
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
    }

    public boolean inMeleeForm()
    {
        return NPCs.getNearest(NpcID.PHANTOM_MUSPAH_12078) != null;
    }
}