package com.lucidplugins.lucidspices.api.util;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.packets.MousePackets;
import net.unethicalite.api.packets.WidgetPackets;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class DialogUtils
{
    public static void queueResumePauseDialog(int widgetId, int childId)
    {
        MousePackets.queueClickPacket();
        WidgetPackets.queueResumePauseWidgetPacket(widgetId, childId);
    }

    public static List<String> getOptions()
    {
        Widget widget = Static.getClient().getWidget(219, 1);
        if (widget == null || widget.isSelfHidden())
        {
            return Collections.emptyList();
        }
        else
        {
            List<String> out = new ArrayList();
            Widget[] children = widget.getChildren();
            if (children == null)
            {
                return out;
            }
            else
            {
                for (int i = 1; i < children.length; ++i)
                {
                    if (children[i] != null && !children[i].getText().isBlank())
                    {
                        out.add(children[i].getText());
                    }
                }

                return out;
            }
        }
    }

    public static int getOptionIndex(String option)
    {
        if (getOptions().isEmpty())
        {
            return -1;
        }

        List<String> options = getOptions();
        for (int index = 0; index < options.size(); index++)
        {
            if (options.get(index).contains(option))
            {
                return index + 1;
            }
        }

        return -1;
    }
    public static boolean hasOption(Client client, String option)
    {
        return hasOption(client, s -> s.equalsIgnoreCase(option));
    }

    public static boolean hasOption(Client client, Predicate<String> option)
    {
        return getOptions(client).stream().map(Widget::getText).filter(Objects::nonNull).anyMatch(option);
    }

    public static List<Widget> getOptions(Client client)
    {
        Widget widget = client.getWidget(219, 1);
        if (!Widgets.isVisible(widget))
        {
            return Collections.emptyList();
        }
        else
        {
            List<Widget> out = new ArrayList();
            Widget[] children = widget.getChildren();
            if (children == null)
            {
                return out;
            }
            else
            {
                for (int i = 1; i < children.length; ++i)
                {
                    if (!children[i].getText().isBlank())
                    {
                        out.add(children[i]);
                    }
                }

                return out;
            }
        }
    }
}
