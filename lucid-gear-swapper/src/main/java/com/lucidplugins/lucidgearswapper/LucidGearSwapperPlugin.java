package com.lucidplugins.lucidgearswapper;

import com.google.common.collect.Lists;
import com.google.inject.Provides;
import com.lucidplugins.api.item.SlottedItem;
import com.lucidplugins.api.util.EquipmentUtils;
import com.lucidplugins.api.util.InventoryUtils;
import com.lucidplugins.api.util.MessageUtils;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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

    @Inject
    private KeyManager keyManager;

    private String[] configs = new String[6];

    private GearSwapState gearSwapState = GearSwapState.TICK_1;

    private int gearSwapSelected = -1;

    private List<Integer> lastItemsEquipped = new ArrayList<>();

    private List<String> lastEquipmentList = new ArrayList<>();

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
        keyManager.unregisterKeyListener(this);
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
        getEquipmentChanges();

        if (lastItemsEquipped.size() > 0)
        {
            for (int i = 0; i < configs.length; i++)
            {
                if (!isActivateOnFirstItem(i) || configs[i] == null)
                {
                    continue;
                }

                List<String> configList = parseList(configs[i]);
                if (configList == null || configList.size() == 0)
                {
                    continue;
                }

                String itemString = configList.get(0);
                List<SlottedItem> firstItem = EquipmentUtils.getAll().stream().filter(item -> client.getItemDefinition(item.getItem().getId()).getName().contains(itemString)).collect(Collectors.toList());

                if (firstItem.size() > 0)
                {
                    if (lastItemsEquipped.contains(firstItem.get(0).getItem().getId()) && gearSwapSelected == -1)
                    {
                        if (isSlotEnabled(i))
                        {
                            gearSwapSelected = i;
                        }
                    }
                }
            }

            lastItemsEquipped.clear();
        }

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
    private void getEquipmentChanges()
    {
        Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankWidget != null && bankWidget.isVisible())
        {
            return;
        }

        final List<SlottedItem> equippedItems = EquipmentUtils.getAll();
        final List<String> itemsMapped = equippedItems.stream().map(item -> client.getItemDefinition(item.getItem().getId()).getName()).collect(Collectors.toList());

        if (!listsMatch(itemsMapped, lastEquipmentList))
        {
            for (SlottedItem slottedItem : equippedItems)
            {
                String name = client.getItemComposition(slottedItem.getItem().getId()).getName();
                if (!lastEquipmentList.contains(name))
                {
                    if (gearSwapSelected == -1)
                    {
                        lastItemsEquipped.add(slottedItem.getItem().getId());
                    }
                }
            }
            lastEquipmentList.clear();
            lastEquipmentList.addAll(itemsMapped);
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
        keyManager.registerKeyListener(this);
        parseSwaps();
    }

    private void parseSwaps()
    {
        configs[0] = config.swap1String();
        configs[1] = config.swap2String();
        configs[2] = config.swap3String();
        configs[3] = config.swap4String();
        configs[4] = config.swap5String();
        configs[5] = config.swap6String();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.copyGearHotkey().matches(e))
        {
            if (client == null || client.getGameState() != GameState.LOGGED_IN)
            {
                return;
            }

            int slotSelected = getSlotFromGearSlotSelected(config.slotToCopyTo());

            if (slotSelected != 0)
            {
                final List<SlottedItem> equippedGear = EquipmentUtils.getAll();
                equippedGear.sort(Comparator.comparing(item -> slotOrderToCopy.indexOf(item.getSlot())));

                String equippedItemsString = equippedGear.stream().map(slottedItem -> client.getItemDefinition(slottedItem.getItem().getId()).getName()).collect(Collectors.joining(","));
                String key = "swap" + slotSelected + "String";
                configManager.setConfiguration("lucid-gear-swapper", key, equippedItemsString);
                clientThread.invoke(() -> MessageUtils.addMessage(client, "Copied Equipment to Preset Slot " + slotSelected));
            }
        }

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
        List<String> itemList = parseList(configs[swapId]);
        List<SlottedItem> items = InventoryUtils.getAll(item -> itemList.contains(client.getItemDefinition(item.getItem().getId()).getName()));/*Inventory.getAll(Utils.itemConfigList(configLists[swapId], true, false))*/;

        if (items != null)
        {
            if (swapFirstHalf)
            {
                for (int i = 0; i < items.size() / 2; i++)
                {
                    SlottedItem item = items.get(i);
                    if (InventoryUtils.itemHasAction(client, item.getItem().getId(), "Wield"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wield");
                    }
                    else if (InventoryUtils.itemHasAction(client, item.getItem().getId(), "Wear"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wear");
                    }
                }
            }
            else
            {
                for (SlottedItem item : items)
                {
                    if (InventoryUtils.itemHasAction(client, item.getItem().getId(), "Wield"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wield");
                    }
                    else if (InventoryUtils.itemHasAction(client, item.getItem().getId(), "Wear"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wear");
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

    public List<String> parseList(String items)
    {
        return Arrays.stream(items.split(",")).collect(Collectors.toList());
    }

    public boolean listsMatch(List<String> list1, List<String> list2)
    {
        if (list1.size() != list2.size())
        {
            return false;
        }

        List<String> list2Copy = Lists.newArrayList(list2);
        for (String element : list1)
        {
            if (!list2Copy.remove(element))
            {
                return false;
            }
        }

        return list2Copy.isEmpty();
    }

    enum GearSwapState
    {
        TICK_1, TICK_2, FINISHED
    }
}