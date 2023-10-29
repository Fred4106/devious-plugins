package net.unethicalite.plugins.lucidcannonreloader

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.ChatMessage
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOpened
import net.runelite.api.events.VarbitChanged
import net.runelite.api.queries.GameObjectQuery
import net.runelite.client.callback.ClientThread
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.PluginManager
import net.unethicalite.api.commons.Rand
import net.unethicalite.api.items.Inventory
import net.unethicalite.api.utils.MessageUtils
import org.pf4j.Extension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
@Extension
@PluginDescriptor(
    name = "Lucid Cannon Reloader",
    description = "A plugin that will reload your cannon so you don't have to",
    tags = ["lucid", "spice", "spices", "cat", "hellrat"])

class LucidCannonReloaderPlugin : Plugin()
{
    @Inject
    private lateinit var config: LucidCannonReloaderConfig

    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var clientThread: ClientThread

    private var cballsLeft = 0

    private var nextReloadAmount = 0

    private var nextReloadDelay = 0

    private var lastReloadAttempt = 0

    private var cannonLocation: WorldPoint? = null

    private var goodDelayRange = false

    private var goodReloadRange = false

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun startUp() {
        log.info("Started Lucid Cannon Reloader")

        this.clientThread.invoke(Runnable {
            this.cballsLeft = client.getVarpValue(VarPlayer.CANNON_AMMO)
        })

        goodDelayRange = if (config.minReloadDelay() > config.maxReloadDelay()) {
            MessageUtils.addMessage("Minimum reload delay must be less than or equal to maximum!")
            false
        } else {
            true
        }

        goodReloadRange = if (config.minCannonballAmount() > config.maxCannonballAmount()) {
            MessageUtils.addMessage("Minimum cannonball amount must be less than or equal to maximum!")
            false
        } else {
            true
        }

        nextReloadAmount = Rand.nextInt(config.minCannonballAmount(), config.maxCannonballAmount())
    }

    override fun shutDown() {
        log.info("Stopped Lucid Cannon Reloader")
    }

    @Provides
    fun provideConfig(configManager: ConfigManager): LucidCannonReloaderConfig {
        return configManager.getConfig(LucidCannonReloaderConfig::class.java)
    }

    @Subscribe
    private fun onGameTick(event: GameTick)
    {
        val cannon = getCannon()?: return

        if (!goodDelayRange || !goodReloadRange)
        {
            return
        }

        if (!(Inventory.contains("Cannonball") || Inventory.contains("Granite cannonball")))
        {
            if (ticksSinceLastReloadAttempt() > 15)
            {
                MessageUtils.addMessage("Out of cannonballs!")
                lastReloadAttempt = client.tickCount
            }
            return
        }

        if (cballsLeft < nextReloadAmount)
        {
            if (ticksSinceLastReloadAttempt() > nextReloadDelay)
            {
                cannon.interact("Fire")

                lastReloadAttempt = client.tickCount
                nextReloadAmount = Rand.nextInt(config.minCannonballAmount(), config.maxCannonballAmount())
                nextReloadDelay = Rand.nextInt(config.minReloadDelay(), config.maxReloadDelay())
            }
        }
    }

    @Subscribe
    fun onChatMessage(event: ChatMessage)
    {
        if (event.type != ChatMessageType.GAMEMESSAGE)
        {
            return
        }

        if (event.message.contains("That isn't your cannon") && ticksSinceLastReloadAttempt() < 10)
        {
            cannonLocation = null
        }
    }

    @Subscribe
    fun onConfigChanged(event: ConfigChanged)
    {
        if (!event.group.equals("lucid-cannon-reloader"))
        {
            return
        }

        goodDelayRange = if (config.minReloadDelay() > config.maxReloadDelay()) {
            MessageUtils.addMessage("Minimum reload delay must be less than or equal to maximum!")
            false
        } else {
            true
        }

        goodReloadRange = if (config.minCannonballAmount() > config.maxCannonballAmount()) {
            MessageUtils.addMessage("Minimum cannonball amount must be less than or equal to maximum!")
            false
        } else {
            true
        }
    }

    @Subscribe
    fun onVarbitChanged(varbitChanged: VarbitChanged) {
        if (varbitChanged.varpId == VarPlayer.CANNON_AMMO.id) {
            cballsLeft = varbitChanged.value
        }
    }

    @Subscribe
    private fun onMenuOpened(event: MenuOpened)
    {
        val fireEntry = event.menuEntries.filter { menuEntry ->
                    menuEntry.option.equals("Fire") &&
                    menuEntry.target.contains("Dwarf multicannon")
        }

        if (fireEntry.isEmpty())
        {
            return
        }

        val targetLocalX = fireEntry.first().param0
        val targetLocalY = fireEntry.first().param1

        val targetWorldPoint = WorldPoint.fromScene(client, targetLocalX, targetLocalY, client.localPlayer.plane)

        if (targetWorldPoint == cannonLocation)
        {
            client.createMenuEntry(1)
                .setOption("<col=00ff00>Lucid Cannon Reloader</col>")
                .setTarget("Un-claim Cannon")
                .setType(MenuAction.RUNELITE)
                .onClick {
                    cannonLocation = null
                    MessageUtils.addMessage("Cannon un-claimed")
                }
        }
        else
        {
            client.createMenuEntry(1)
                .setOption("<col=00ff00>Lucid Cannon Reloader</col>")
                .setTarget("Claim Cannon")
                .setType(MenuAction.RUNELITE)
                .onClick {
                    cannonLocation = targetWorldPoint
                    MessageUtils.addMessage("Cannon claimed")
                }
        }

    }

    private fun ticksSinceLastReloadAttempt() = client.tickCount - lastReloadAttempt

    private fun getCannon(): GameObject?
    {
        return GameObjectQuery().filter { gameObject -> gameObject.name.contains("Dwarf multicannon") && gameObject.worldLocation.dx(-1).dy(-1) == cannonLocation }.result(client).first()
    }
}