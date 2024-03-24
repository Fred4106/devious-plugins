package com.fredplugins.gauntletV2;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.utils.MessageUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import com.fredplugins.gauntletV2.DemibossType;
import scala.Option;
import scala.Option$;

@Extension
@PluginDescriptor(
        name = "Fred GauntletV2",
        description = "Helps with Gauntlet",
        enabledByDefault = false,
        tags = {"gauntlet"}
)
public class GauntletV2Plugin extends Plugin
{

    @Inject
    private Client client;
    @Inject
    private EventBus eventBus;
    @Inject
    private GauntletV2Config config;

    private final Logger log = LoggerFactory.getLogger(GauntletV2Plugin.class);
//            Logger.getLogger(getName());

    private PluginState pluginState = null;

    @Provides
    GauntletV2Config getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(GauntletV2Config.class);
    }

    @Override
    protected void startUp()
    {
        log.info("{} Started", getName());
        pluginState = PluginState$.MODULE$.create(client);
        eventBus.register(pluginState);
    }

    @Override
    protected void shutDown()
    {
        log.info("{} Stopped", getName());
        eventBus.unregister(pluginState);
        pluginState = null;
    }
}