package net.unethicalite.plugins.lucidgearswapper;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ConfigButtonClicked;
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
import net.unethicalite.api.events.InventoryChanged;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.client.Static;
import net.unethicalite.plugins.lucidgearswapper.util.ConfigList;
import net.unethicalite.plugins.lucidgearswapper.util.Utils;
import org.pf4j.Extension;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
        name = "Lucid Gear Swapper",
        description = "Set-up up to 6 custom gear swaps with customizable hotkeys or trigger them via weapon equip",
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
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    private ConfigList[] configLists = new ConfigList[6];

    private GearSwapState gearSwapState = GearSwapState.TICK_1;

    private int gearSwapSelected = -1;

    private int lastInventoryItemRemovedId = -1;

    private final List<Integer> slotOrderToCopy = List.of(EquipmentInventorySlot.WEAPON.getSlotIdx(), EquipmentInventorySlot.SHIELD.getSlotIdx(), EquipmentInventorySlot.HEAD.getSlotIdx(), EquipmentInventorySlot.BODY.getSlotIdx(),
                                                         EquipmentInventorySlot.LEGS.getSlotIdx(), EquipmentInventorySlot.CAPE.getSlotIdx(), EquipmentInventorySlot.BOOTS.getSlotIdx(), EquipmentInventorySlot.AMULET.getSlotIdx(),
                                                         EquipmentInventorySlot.GLOVES.getSlotIdx(), EquipmentInventorySlot.RING.getSlotIdx(), EquipmentInventorySlot.AMMO.getSlotIdx());

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

    @Subscribe
    private void onConfigButtonClicked(final ConfigButtonClicked event)
    {
        if (!event.getGroup().equals("lucid-gear-swapper"))
        {
            return;
        }

        if (!event.getKey().equals("copyGearButton"))
        {
            return;
        }

        if (client == null || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        int slotSelected = getSlotFromGearSlotSelected(config.slotToCopyTo());

        if (slotSelected != 0)
        {
            final List<Item> equippedGear = Equipment.getAll();
            equippedGear.sort(Comparator.comparing(item -> slotOrderToCopy.indexOf(item.getSlot())));

            String equippedItemsString = equippedGear.stream().map(Item::getName).collect(Collectors.joining(","));
            String key = "swap" + slotSelected + "String";
            configManager.setConfiguration("lucid-gear-swapper", key, equippedItemsString);
            MessageUtils.addMessage("Copied Equipment to Preset Slot " + slotSelected);
        }

    }

    @Subscribe
    private void onInventoryChanged(final InventoryChanged event)
    {
        if (event.getChangeType() == InventoryChanged.ChangeType.ITEM_REMOVED)
        {
            if (Bank.isOpen())
            {
                return;
            }

            lastInventoryItemRemovedId = event.getItemId();
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

                List<Item> firstItem = Arrays.stream(event.getItemContainer().getItems()).filter(item -> item.getName().contains(itemString)).collect(Collectors.toList());

                if (firstItem.size() > 0)
                {
                    if (lastInventoryItemRemovedId == firstItem.get(0).getId() && gearSwapSelected == -1)
                    {
                        if (isSlotEnabled(gearSwapSelected))
                        {
                            gearSwapSelected = i;
                        }
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

        if (config.swap1Hotkey().matches(e) && config.swap1Enabled())
        {
            gearSwapSelected = 0;
        }

        if (config.swap2Hotkey().matches(e) && config.swap2Enabled())
        {
            gearSwapSelected = 1;
        }

        if (config.swap3Hotkey().matches(e) && config.swap3Enabled())
        {
            gearSwapSelected = 2;
        }

        if (config.swap4Hotkey().matches(e) && config.swap4Enabled())
        {
            gearSwapSelected = 3;
        }

        if (config.swap5Hotkey().matches(e) && config.swap5Enabled())
        {
            gearSwapSelected = 4;
        }

        if (config.swap6Hotkey().matches(e) && config.swap6Enabled())
        {
            gearSwapSelected = 5;
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

    private int getSlotFromGearSlotSelected(LucidGearSwapperConfig.GearSlot slot)
    {
        switch (slot)
        {
            case GEAR_SLOT_1:
                return 1;
            case GEAR_SLOT_2:
                return 2;
            case GEAR_SLOT_3:
                return 3;
            case GEAR_SLOT_4:
                return 4;
            case GEAR_SLOT_5:
                return 5;
            case GEAR_SLOT_6:
                return 6;
            default:
                return 0;
        }
    }

    private boolean isSlotEnabled(int slot)
    {
        switch (slot)
        {
            case 0:
                return config.swap1Enabled();
            case 1:
                return config.swap2Enabled();
            case 2:
                return config.swap3Enabled();
            case 3:
                return config.swap4Enabled();
            case 4:
                return config.swap5Enabled();
            case 5:
                return config.swap6Enabled();
            default:
                return false;
        }
    }

    enum GearSwapState
    {
        TICK_1, TICK_2, FINISHED
    }
}