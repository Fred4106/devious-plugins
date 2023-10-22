package net.unethicalite.plugins.lucidspices

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.Range

@ConfigGroup("lucid-spices")
interface LucidSpicesConfig : Config
{
    @ConfigItem(
        keyName = "fullAuto",
        name = "Full Auto",
        description = "Plugin does everything for you. Stand next to the curtain that has the behemoth you want to kill and toggle on.",
        position = 0
    )
    fun fullAuto() : Boolean = false

    @ConfigItem(
        keyName = "foodId",
        name = "Food Item ID",
        description = "The ID of the food item you want to feed the cat. Default is Raw karambwanji",
        position = 1
    )
    fun foodId() : Int = 3150

    @ConfigItem(
        keyName = "healPercent",
        name = "Heal Below Health %",
        description = "The plugin will heal your cat if it is less than this health percentage during a fight",
        position = 2
    )
    @Range(min = 1, max = 100)
    fun healPercent() : Int = 51
}