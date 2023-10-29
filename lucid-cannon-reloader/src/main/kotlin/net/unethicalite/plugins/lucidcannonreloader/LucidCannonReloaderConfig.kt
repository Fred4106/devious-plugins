package net.unethicalite.plugins.lucidcannonreloader

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.Range

@ConfigGroup("lucid-cannon-reloader")
interface LucidCannonReloaderConfig : Config
{
    @ConfigItem(
        keyName = "minCannonballAmount",
        name = "Min. Cannonballs",
        description = "The absolute minimum amount of cannonballs left before a reload is needed",
        position = 0
    )
    @Range(min = 1, max = 30)
    fun minCannonballAmount() : Int = 3
    @ConfigItem(
        keyName = "maxCannonballAmount",
        name = "Max. Cannonballs",
        description = "The absolute maximum amount of cannonballs left before a reload is needed",
        position = 1
    )
    @Range(min = 1, max = 30)
    fun maxCannonballAmount() : Int = 15
    @ConfigItem(
        keyName = "minReloadDelay",
        name = "Min. Reload Delay",
        description = "Minimum amount of game ticks plugin will wait to reload the cannon when below reload amount (Each game tick is ~600ms)",
        position = 2
    )
    @Range(min = 1, max = 20)
    fun minReloadDelay() : Int = 3
    @ConfigItem(
        keyName = "maxReloadDelay",
        name = "Max. Reload Delay",
        description = "Maximum amount of game ticks plugin will wait to reload the cannon when below reload amount (Each game tick is ~600ms)",
        position = 3
    )
    @Range(min = 1, max = 20)
    fun maxReloadDelay() : Int = 5
}