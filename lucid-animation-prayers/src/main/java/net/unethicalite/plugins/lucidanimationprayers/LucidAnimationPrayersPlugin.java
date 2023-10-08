package net.unethicalite.plugins.lucidanimationprayers;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
@PluginDescriptor(
        name = "Lucid Animation Prayers",
        description = "Set up auto prayers based on animation ID",
        enabledByDefault = false,
        tags = {"prayer", "swap"}
)
@Singleton
public class LucidAnimationPrayersPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidAnimationPrayersConfig config;

    @Inject
    private OverlayManager overlayManager;


    private int lastUpdateTick = -1;

    private Map<Integer, List<AnimationPrayer>> prayerMap = new HashMap<>();

    private List<ScheduledPrayer> scheduledPrayers = new ArrayList<>();

    @Provides
    LucidAnimationPrayersConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidAnimationPrayersConfig.class);
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
    private void onAnimationChanged(final AnimationChanged event)
    {
        List<AnimationPrayer> prayers = prayerMap.get(event.getActor().getAnimation());
        if (prayers == null || prayers.isEmpty())
        {
            return;
        }

        for (AnimationPrayer prayer : prayers)
        {
            scheduledPrayers.add(new ScheduledPrayer(prayer.getPrayerToActivate(), client.getTickCount() + prayer.getTickDelay()));
        }
    }

    @Subscribe
    private void onConfigChanged(final ConfigChanged event)
    {
        if (!event.getGroup().equals("lucid-animation-prayers"))
        {
            return;
        }

        parsePrayers();
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (lastUpdateTick != client.getTickCount())
        {
            lastUpdateTick = client.getTickCount();

            for (ScheduledPrayer prayer : scheduledPrayers)
            {
                if (client.getTickCount() == prayer.getActivationTick())
                {
                    activatePrayer(prayer.getPrayer());
                }
            }

            scheduledPrayers.removeIf(prayer -> prayer.getActivationTick() <= client.getTickCount() - 1);
        }
    }

    private void pluginEnabled()
    {
        parsePrayers();
    }

    private void parsePrayers()
    {
        prayerMap.clear();
        for (int i = 1; i < 7; i++)
        {
            parsePrayerSlot(i);
        }
    }

    private void parsePrayerSlot(int id)
    {
        List<Integer> ids = List.of();
        List<Integer> delays = List.of();
        Prayer prayChoice = null;
        switch (id)
        {
            case 1:
                ids = intListFromString(config.pray1Ids());
                delays = intListFromString(config.pray1delays());
                prayChoice = config.pray1choice();
                break;
            case 2:
                ids = intListFromString(config.pray2Ids());
                delays = intListFromString(config.pray2delays());
                prayChoice = config.pray2choice();
                break;
            case 3:
                ids = intListFromString(config.pray3Ids());
                delays = intListFromString(config.pray3delays());
                prayChoice = config.pray3choice();
                break;
            case 4:
                ids = intListFromString(config.pray4Ids());
                delays = intListFromString(config.pray4delays());
                prayChoice = config.pray4choice();
                break;
            case 5:
                ids = intListFromString(config.pray5Ids());
                delays = intListFromString(config.pray5delays());
                prayChoice = config.pray5choice();
                break;
            case 6:
                ids = intListFromString(config.pray6Ids());
                delays = intListFromString(config.pray6delays());
                prayChoice = config.pray6choice();
                break;
        }

        if (ids.isEmpty() || prayChoice == null)
        {
            return;
        }

        populateAnimationPrayersList(ids, delays, prayChoice);
    }

    private List<Integer> intListFromString(String stringList)
    {
        List<Integer> ints = new ArrayList<>();
        if (stringList == null || stringList.trim().equals(""))
        {
            return ints;
        }

        if (stringList.contains(","))
        {
            String[] intStrings = stringList.split(",");
            for (String s : intStrings)
            {
                try
                {
                    int anInt = Integer.parseInt(s);
                    ints.add(anInt);
                }
                catch (NumberFormatException e)
                {
                }
            }
        }
        else
        {
            try
            {
                int anInt = Integer.parseInt(stringList);
                ints.add(anInt);
            }
            catch (NumberFormatException e)
            {
            }
        }

        return ints;
    }

    private void populateAnimationPrayersList(List<Integer> ids, List<Integer> delays, Prayer prayer)
    {
        if (!delays.isEmpty() && delays.size() != ids.size())
        {
            if (client.getGameState() == GameState.LOGGED_IN)
            {
                MessageUtils.addMessage("If delays are specified, delays and ids list must be the same length!");
            }
        }
        for (int i = 0; i < ids.size(); i++)
        {
            if (!delays.isEmpty())
            {
                addAnimationPrayer(ids.get(i), delays.get(i), prayer);
            }
            else
            {
                addAnimationPrayer(ids.get(i), 0, prayer);
            }
        }
    }

    private void addAnimationPrayer(int id, int delay, Prayer prayer)
    {
        if (!hasPrayerForId(id, prayer))
        {
            List<AnimationPrayer> prayers = prayerMap.get(id);
            if (prayers == null)
            {
                prayers = new ArrayList<>();
            }
            prayers.add(new AnimationPrayer(prayer, delay));
            prayerMap.put(id, prayers);
        }
    }

    private boolean hasPrayerForId(int id, Prayer prayer)
    {
        List<AnimationPrayer> prayers = prayerMap.get(id);
        if (prayers == null || prayers.isEmpty())
        {
            return false;
        }
        for (AnimationPrayer animPrayer : prayers)
        {
            if (animPrayer.getPrayerToActivate().equals(prayer))
            {
                return true;
            }
        }
        return false;
    }

    private static void activatePrayer(Prayer prayer)
    {
        if (Prayers.isEnabled(prayer))
        {
            return;
        }

        Widget widget = Widgets.get(prayer.getWidgetInfo());
        if (widget != null)
        {
            widget.interact(0);
        }
    }
}