package com.fredplugins.exampleplugin;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.utils.MessageUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Singleton;

@Extension
@PluginDescriptor(
        name = "Fred Scala Example",
        description = "An example plugin written in scala that can be copied to use as a plugin skeleton. Does nothing functionally.",
        enabledByDefault = false,
        tags = {"example"}
)
public class FredExamplePlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private FredExampleConfig config;

    private final Logger log = LoggerFactory.getLogger(FredExamplePlugin.class);
//            Logger.getLogger(getName());

    @Provides
    FredExampleConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(FredExampleConfig.class);
    }

    @Override
    protected void startUp()
    {
        log.info("{} Started", getName());

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            Demiboss.DemibossType tpe = Demiboss.DemibossType$.MODULE$.fromId(NpcID.CRYSTALLINE_DRAGON).getOrElse(null);
            log.debug("Demiboss.fromId({}) = {}", NpcID.CRYSTALLINE_DRAGON, tpe);
            MessageUtils.addMessage(getName() + " Started");
        }
    }

    @Override
    protected void shutDown()
    {
        log.info("{} Stopped", getName());
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            MessageUtils.addMessage(getName() + " Stopped");
        }
    }
}