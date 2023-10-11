package net.unethicalite.plugins.lucidgearswapper.util;

import lombok.Getter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 Credit: https://github.com/yuri-moens/
 */
public class ConfigList
{

    @Getter
    private final Map<Integer, Integer> integers;
    @Getter
    private final Map<String, Integer> strings;

    private ConfigList()
    {
        integers = new LinkedHashMap<>();
        strings = new LinkedHashMap<>();
    }

    public static ConfigList parseList(String list)
    {
        final ConfigList configList = new ConfigList();

        for (String string : list.split("[\\n;,]"))
        {
            if (string.equals(""))
            {
                continue;
            }

            // get rid of any comments and trim what's left
            string = string.split("//")[0].trim();
            int amount = 1;

            if (string.contains(":"))
            {
                final String[] splitString = string.split(":");
                amount = Integer.parseInt(splitString[1]);
                string = splitString[0];
            }

            try
            {
                configList.integers.put(Integer.parseInt(string), amount);
            }
            catch (NumberFormatException e)
            {
                configList.strings.put(string, amount);
            }
        }

        return configList;
    }

    public String firstItemInStrings()
    {
        for (String s : strings.keySet())
        {
            return s;
        }
        return null;
    }

    public boolean isEmpty()
    {
        return strings.isEmpty() && integers.isEmpty();
    }
}