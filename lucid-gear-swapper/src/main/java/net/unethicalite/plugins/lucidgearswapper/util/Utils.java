package net.unethicalite.plugins.lucidgearswapper.util;

import net.runelite.api.Item;
import net.unethicalite.api.commons.Predicates;

import java.util.function.Predicate;

/**
 Credit: https://github.com/yuri-moens/
 */
public class Utils
{

    public static <T extends Item> Predicate<T> itemConfigList(ConfigList configList)
    {
        return itemConfigList(configList, false);
    }

    public static <T extends Item> Predicate<T> itemConfigList(ConfigList configList,
                                                               boolean stringContains)
    {
        return itemConfigList(configList, stringContains, true);
    }

    public static <T extends Item> Predicate<T> itemConfigList(ConfigList configList,
                                                               boolean stringContains, boolean caseSensitive)
    {
        final Predicate<T> identifiablePredicate = Predicates.ids(configList.getIntegers().keySet());

        if (!stringContains)
        {
            final Predicate<T> nameablePredicate = Predicates.names(configList.getStrings().keySet());

            return nameablePredicate.or(identifiablePredicate);
        }
        else
        {
            final Predicate<T> nameablePredicate = caseSensitive
                    ? Predicates.nameContains(configList.getStrings().keySet())
                    : Predicates.nameContains(configList.getStrings().keySet(), false);

            return nameablePredicate.or(identifiablePredicate);
        }
    }
}
