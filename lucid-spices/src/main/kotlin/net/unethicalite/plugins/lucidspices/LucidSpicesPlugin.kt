package net.unethicalite.plugins.lucidspices

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.queries.InventoryItemQuery
import net.runelite.api.queries.NPCQuery
import net.runelite.api.queries.WallObjectQuery
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginInstantiationException
import net.runelite.client.plugins.PluginManager
import net.runelite.client.plugins.gpu.GpuPlugin
import net.unethicalite.api.commons.Rand
import net.unethicalite.api.input.Keyboard
import net.unethicalite.api.items.Inventory
import net.unethicalite.api.movement.Movement
import net.unethicalite.api.widgets.Dialog
import org.pf4j.Extension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.SwingUtilities
import kotlin.math.floor

@Extension
@PluginDescriptor(
    name = "Lucid Spices",
    description = "A plugin to help you gather spices for stews and not kill your cat in the process",
    tags = ["lucid", "spice", "spices", "cat", "hellrat"])
class LucidSpicesPlugin : Plugin()
{
    @Inject
    private lateinit var config: LucidSpicesConfig

    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var pluginManager: PluginManager

    private var lastHeal = 0
    private var lastInteract = 0

    private var shutdown = false

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun startUp() {
        log.info("Started Lucid Spices")
        shutdown = false
    }

    override fun shutDown() {
        log.info("Stopped Lucid Spices")
        shutdown = true
    }

    @Provides
    fun provideConfig(configManager: ConfigManager): LucidSpicesConfig {
        return configManager.getConfig(LucidSpicesConfig::class.java)
    }

    @Subscribe
    private fun onGameTick(event: GameTick)
    {
        if (shutdown)
        {
            SwingUtilities.invokeLater {
                try {
                    pluginManager.setPluginEnabled(this, false)
                    pluginManager.stopPlugin(this)
                } catch (ex: PluginInstantiationException) {
                    log.error("error stopping plugin", ex)
                }
            }
        }

        runAwayIfLowHealth()

        handleHealing()

        if (config.fullAuto())
        {
            handleEnteringFight()
        }
    }


    private fun handleHealing()
    {
        if (ticksSinceLastHeal() < Rand.nextInt(4, 5))
        {
            return
        }

        val food = getFood()?: return
        val cat = getCat()?: return
        val curtain = getCurtain()?: return

        val hpPercentage = floor(cat.healthRatio.toDouble() / cat.healthScale.toDouble() * 100).toInt()

        if (hpPercentage !in 1 until config.healPercent())
        {
            return
        }

        log.info("Healing cat with ${food.name}")

        useItemOnWallObject(food, curtain)

        lastHeal = client.tickCount
    }

    private fun runAwayIfLowHealth()
    {
        val cat = getCat()?: return
        val food = getFood()

        val hpPercentage = floor(cat.healthRatio.toDouble() / cat.healthScale.toDouble() * 100).toInt()

        if (hpPercentage !in 1 until config.healPercent())
        {
            return
        }

        if (food != null)
        {
            return
        }

        if (client.localPlayer.worldLocation.x < 3100)
        {
            return
        }

        shutdown = true

        when (client.localPlayer.orientation)
        {
            1536 -> Movement.walk(client.localPlayer.worldLocation.dx(-3))
            1024 -> Movement.walk(client.localPlayer.worldLocation.dy(-3))
            512 -> Movement.walk(client.localPlayer.worldLocation.dx(3))
            0 -> Movement.walk(client.localPlayer.worldLocation.dy(3))
        }

        return
    }

    private fun handleEnteringFight()
    {
        getCat()?: return

        if (Dialog.isOpen())
        {
            selectDialogOption()
        }
        else
        {
            enterFight()
        }
    }

    private fun getCat() : NPC? = NPCQuery().filter { it.name.contains("cat", true) && it.hasAction("Pick-up") }.result(client).nearestTo(client.localPlayer)

    private fun getCurtain(): WallObject? = WallObjectQuery().nameEquals("Curtain").result(client).nearestTo(client.localPlayer)

    private fun getFood() : Item? = InventoryItemQuery(InventoryID.INVENTORY).idEquals(config.foodId()).result(client).first()

    private fun ticksSinceLastHeal() = client.tickCount - lastHeal

    private fun ticksSinceLastInteract() = client.tickCount - lastInteract

    private fun useItemOnWallObject(item : Item, wallObject: WallObject)
    {
        client.invokeMenuAction("Use", "<col=ff9040>" + item.name + "</col>", 0, MenuAction.WIDGET_TARGET.id, item.slot, item.calculateWidgetId(WidgetInfo.INVENTORY), item.id, -1)
        client.invokeMenuAction("Use", "<col=ff9040>" + item.name + "</col><col=ffffff> -> <col=ffff>" + wallObject.name, wallObject.id, MenuAction.WIDGET_TARGET_ON_GAME_OBJECT.id, wallObject.localLocation.sceneX, wallObject.localLocation.sceneY, -1, -1)
    }

    private fun selectDialogOption()
    {
        if (ticksSinceLastInteract() < Rand.nextInt(3, 5))
        {
            return
        }

        if (Dialog.canContinue())
        {
            Dialog.continueSpace()
        }
        else
        {
            if (!Dialog.hasOption("Don't insert your cat."))
            {
                return
            }

            Keyboard.type(1)
        }

        lastInteract = client.tickCount
    }

    private fun enterFight()
    {
        if (client.localPlayer.worldLocation.x > 3100 || ticksSinceLastInteract() < Rand.nextInt(15, 17) || Inventory.isFull())
        {
            return
        }

        val curtain = getCurtain() ?: return

        curtain.interact("Enter")

        lastInteract = client.tickCount
    }
}