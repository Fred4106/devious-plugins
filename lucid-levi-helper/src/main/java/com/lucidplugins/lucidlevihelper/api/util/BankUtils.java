package com.lucidplugins.lucidlevihelper.api.util;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.items.Bank;
import net.unethicalite.client.Static;

public class BankUtils
{

    public static boolean isOpen()
    {
        Widget bankWidget = Static.getClient().getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankWidget != null && !bankWidget.isSelfHidden())
        {
            return true;
        }

        return false;
    }

    public static void close()
    {
        Bank.close();
    }

}
