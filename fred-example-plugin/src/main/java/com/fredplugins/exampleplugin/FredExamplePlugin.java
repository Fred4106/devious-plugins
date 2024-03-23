package com.fredplugins.exampleplugin;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.utils.MessageUtils;
import org.pf4j.Extension;
import javax.inject.Inject;
import java.util.logging.Logger;

@Extension
@PluginDescriptor(
        name = "Fred Example",
        description = "An example plugin that can be copied to use as a plugin skeleton. Does nothing functionally.",
        enabledByDefault = false,
        tags = {"example"}
)
public class FredExamplePlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private FredExampleConfig config;

    private Logger log = Logger.getLogger(getName());

    @Provides
    FredExampleConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(FredExampleConfig.class);
    }

    @Override
    protected void startUp()
    {
        log.info(getName() + " Started");

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            MessageUtils.addMessage(getName() + " Started");
        }
    }

    @Override
    protected void shutDown()
    {
        log.info(getName() + " Stopped");
    }
}