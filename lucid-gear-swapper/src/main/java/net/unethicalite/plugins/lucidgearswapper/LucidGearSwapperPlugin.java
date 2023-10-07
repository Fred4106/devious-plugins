package net.unethicalite.plugins.lucidgearswapper;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;
import net.unethicalite.plugins.lucidgearswapper.util.ConfigList;
import net.unethicalite.plugins.lucidgearswapper.util.Utils;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@Extension
@PluginDescriptor(
        name = "Lucid Gear Swapper",
        description = "Set-up up to 6 custom gear swaps with customizable hotkeys",
        enabledByDefault = false,
        tags = {"gear", "swap", "swapper", "hotkey"}
)
@Singleton
public class LucidGearSwapperPlugin extends Plugin implements KeyListener
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidGearSwapperConfig config;

    @Inject
    private OverlayManager overlayManager;

    private ScheduledExecutorService executor;

    private ConfigList configList1;

    private ConfigList configList2;

    private ConfigList configList3;

    private ConfigList configList4;

    private ConfigList configList5;

    private ConfigList configList6;

    private GearSwapState gearSwapState = GearSwapState.TICK_1;

    private int gearSwapSelected = -1;

    private int lastUpdateTick = -1;

    @Provides
    LucidGearSwapperConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidGearSwapperConfig.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    @Override
    protected void shutDown()
    {
        Static.getKeyManager().unregisterKeyListener(this);
    }

    @Subscribe
    private void onConfigChanged(final ConfigChanged event)
    {
        if (!event.getGroup().equals("lucid-gear-swapper"))
        {
            return;
        }

        parseSwaps();
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (lastUpdateTick != client.getTickCount())
        {
            lastUpdateTick = client.getTickCount();
            if (gearSwapSelected != -1)
            {
                if (gearSwapState == GearSwapState.TICK_1)
                {
                    if (config.oneTickSwap())
                    {
                        swap(gearSwapSelected, false);
                        gearSwapState = GearSwapState.FINISHED;
                    }
                    else
                    {
                        swap(gearSwapSelected, true);
                        gearSwapState = GearSwapState.TICK_2;
                    }
                }
                else if (gearSwapState == GearSwapState.TICK_2)
                {
                    swap(gearSwapSelected, false);
                    gearSwapState = GearSwapState.FINISHED;
                }

                if (gearSwapState == GearSwapState.FINISHED)
                {
                    gearSwapSelected = -1;
                    gearSwapState = GearSwapState.TICK_1;
                }
            }
        }
    }

    private void pluginEnabled()
    {
        Static.getKeyManager().registerKeyListener(this);
        parseSwaps();
    }

    private void parseSwaps()
    {
        configList1 = ConfigList.parseList(config.swap1String());
        configList2 = ConfigList.parseList(config.swap2String());
        configList3 = ConfigList.parseList(config.swap3String());
        configList4 = ConfigList.parseList(config.swap4String());
        configList5 = ConfigList.parseList(config.swap5String());
        configList6 = ConfigList.parseList(config.swap6String());
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (gearSwapSelected != -1)
        {
            return;
        }

        if (config.swap1Hotkey().matches(e))
        {
            gearSwapSelected = 1;
        }

        if (config.swap2Hotkey().matches(e))
        {
            gearSwapSelected = 2;
        }

        if (config.swap3Hotkey().matches(e))
        {
            gearSwapSelected = 3;
        }

        if (config.swap4Hotkey().matches(e))
        {
            gearSwapSelected = 4;
        }

        if (config.swap5Hotkey().matches(e))
        {
            gearSwapSelected = 5;
        }

        if (config.swap6Hotkey().matches(e))
        {
            gearSwapSelected = 6;
        }

        if (gearSwapSelected != -1)
        {
            e.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    private void swap(int swapId, boolean swapFirstHalf)
    {
        List<Item> items = null;
        switch (swapId)
        {
            case 1:
                items = Inventory.getAll(Utils.itemConfigList(configList1, true, false));
                break;
            case 2:
                items = Inventory.getAll(Utils.itemConfigList(configList2, true, false));
                break;
            case 3:
                items = Inventory.getAll(Utils.itemConfigList(configList3, true, false));
                break;
            case 4:
                items = Inventory.getAll(Utils.itemConfigList(configList4, true, false));
                break;
            case 5:
                items = Inventory.getAll(Utils.itemConfigList(configList5, true, false));
                break;
            case 6:
                items = Inventory.getAll(Utils.itemConfigList(configList6, true, false));
                break;
        }

        if (items != null)
        {
            if (swapFirstHalf)
            {
                for (int i = 0; i < items.size() / 2; i++)
                {
                    Item item = items.get(i);
                    if (item.hasAction("Wield"))
                    {
                        item.interact("Wield");
                    }
                    else
                    {
                        item.interact("Wear");
                    }
                }
            }
            else
            {
                for (Item item : items)
                {
                    if (item.hasAction("Wield"))
                    {
                        item.interact("Wield");
                    }
                    else
                    {
                        item.interact("Wear");
                    }
                }
            }
        }
    }

    enum GearSwapState
    {
        TICK_1, TICK_2, FINISHED
    }
}