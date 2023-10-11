package net.unethicalite.plugins.lucidgearswapper;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
        name = "Lucid Gear Swapper",
        description = "Set-up up to 6 custom gear swaps with customizable hotkeys and more",
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

    private ConfigList[] configLists = new ConfigList[6];

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

    @Subscribe
    private void onContainerChanged(final ItemContainerChanged event)
    {
        if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
        {
            for (int i = 0; i < configLists.length; i++)
            {
                if (!isActivateOnFirstItem(i))
                {
                    continue;
                }

                String itemString = configLists[i].firstItemInStrings();

                List<Item> equipment = Arrays.stream(event.getItemContainer().getItems()).filter(item -> item.getName().contains(itemString)).collect(Collectors.toList());

                if (equipment.size() > 0)
                {
                    if (gearSwapSelected == -1)
                    {
                        gearSwapSelected = i;
                    }
                    return;
                }
            }
        }
    }

    private boolean isActivateOnFirstItem(int configListIndex)
    {
        switch (configListIndex)
        {
            case 0:
                if (config.equipFirstItem1())
                {
                    return true;
                }
                return false;
            case 1:
                if (config.equipFirstItem2())
                {
                    return true;
                }
                return false;
            case 2:
                if (config.equipFirstItem3())
                {
                    return true;
                }
                return false;
            case 3:
                if (config.equipFirstItem4())
                {
                    return true;
                }
                return false;
            case 4:
                if (config.equipFirstItem5())
                {
                    return true;
                }
                return false;
            case 5:
                if (config.equipFirstItem6())
                {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private void pluginEnabled()
    {
        Static.getKeyManager().registerKeyListener(this);
        parseSwaps();
    }

    private void parseSwaps()
    {
        configLists[0] = ConfigList.parseList(config.swap1String());
        configLists[1] = ConfigList.parseList(config.swap2String());
        configLists[2] = ConfigList.parseList(config.swap3String());
        configLists[3] = ConfigList.parseList(config.swap4String());
        configLists[4] = ConfigList.parseList(config.swap5String());
        configLists[5] = ConfigList.parseList(config.swap6String());
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
        List<Item> items = Inventory.getAll(Utils.itemConfigList(configLists[swapId], true, false));

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