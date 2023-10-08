package net.unethicalite.plugins.exampleplugin;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;

@Extension
@PluginDescriptor(
        name = "Lucid Example",
        description = "An example plugin that can be copied to use as a plugin skeleton. Does nothing functionally.",
        enabledByDefault = false,
        tags = {"example"}
)
@Singleton
public class LucidExamplePlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private net.unethicalite.plugins.exampleplugin.LucidExampleConfig config;

    @Inject
    private OverlayManager overlayManager;

    private int lastUpdateTick = -1;


    @Provides
    net.unethicalite.plugins.exampleplugin.LucidExampleConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(net.unethicalite.plugins.exampleplugin.LucidExampleConfig.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    @Override
    protected void shutDown()
    {

    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (lastUpdateTick != client.getTickCount())
        {
            lastUpdateTick = client.getTickCount();

            // Do something this tick
        }
    }

    private void pluginEnabled()
    {

    }
}