package com.fredplugins.gauntletV2

import com.fredplugins.gauntletV2.overlay.{Overlay, OverlayGauntlet}
import com.google.inject.{Binding, Inject, Injector, Provider, Provides, Singleton}
import net.runelite.api.{Actor, ChatMessageType, Client, GameObject, GameState, HeadIcon, InventoryID, NPC, NpcID, Player}
import net.runelite.api.events.{AnimationChanged, GameObjectDespawned, GameObjectSpawned, GameStateChanged, GameTick, ItemContainerChanged, NpcDespawned, NpcSpawned, VarbitChanged}
import net.runelite.api.queries.GameObjectQuery
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.{EventBus, Subscribe}
import net.runelite.client.plugins.{Plugin, PluginDescriptor}
import net.runelite.client.ui.overlay.OverlayManager
import net.unethicalite.api.utils.MessageUtils
import org.pf4j.Extension
import org.slf4j.{Logger, LoggerFactory}

import scala.util.chaining.*
import scala.reflect.{ClassTag, TypeTest, Typeable}
import scala.collection.mutable
import scala.compiletime.uninitialized

trait Helper { //this: Plugin =>
  def getInjector: Injector

  def inject[T: Typeable](using ct: ClassTag[T]): T = {
    this.getInjector.getBinding(ct.runtimeClass).pipe(b => Option(b.asInstanceOf[Binding[T]])).map(_.getProvider.get()).get
  }
}

@PluginDescriptor(name = "Fred GauntletV3", description = "Helps with Gauntlet", enabledByDefault = false, tags = Array("gauntlet"))
@Extension
@Singleton
class GauntletPluginV3() extends Plugin with Helper {
  private val log: Logger= LoggerFactory.getLogger(classOf[GauntletPluginV3])

  @Provides
  def getConfig(configManager: ConfigManager): GauntletV2Config = configManager.getConfig(classOf[GauntletV2Config])

  lazy val client: Client = inject[Client]
  lazy val eventBus: EventBus = inject[EventBus]
  lazy val configManager: ConfigManager = inject[ConfigManager]
  lazy val overlayManager: OverlayManager = inject[OverlayManager]


  @Inject
  val overlayGauntlet: OverlayGauntlet = null// = //inject[OverlayGauntlet]

  val overlays: scala.collection.mutable.Set[Overlay] = mutable.HashSet.empty[Overlay]

  var npcs: Seq[NPC] = List.empty[NPC]
  var state: Option[HunllefState] = Option.empty[HunllefState]
  var inGauntlet = false
  var inHunllef = false

  @Subscribe
  def onNpcSpawned(event: NpcSpawned): Unit = {
    val npc = event.getNpc
    val npcTpe = NpcType.fromId(npc.getId)
    if(npcTpe.isDefined) {
      npcs = npcs.appended(npc)
    }
    npcTpe.foreach {
      case NpcType.Boss =>
        state = Some(HunllefState.init())
      case NpcType.Tornado => {
        state = state.map(_.spawnTornado())
      }
      case o =>
    }
  }


  @Subscribe
  def onNpcDespawned(event: NpcDespawned): Unit = {
    val npc = event.getNpc
    val npcTpe = NpcType.fromId(npc.getId)
    if (npcTpe.isDefined) {
      npcs = npcs.filter(_ != npc)
    }
    npcTpe.foreach {
      case NpcType.Tornado => {
        state = state.map(_.copy(tornadoTimeRemaining = 0))
      }
      case NpcType.Boss => {
        state = None
      }
      case _ =>
    }
  }

  @Subscribe
  def onGameTick(event: GameTick): Unit = {
    state = state.map(_.gameTick())
  }

  @Subscribe
  def onGameStateChanged(event: GameStateChanged): Unit = {
    event.getGameState match {
      case GameState.LOADING =>
//        resources.clear()
//        utilities.clear()
      case GameState.LOGIN_SCREEN | GameState.HOPPING =>
        shutDown()
      case o =>
    }
  }

  @Subscribe
  def onGameObjectSpawned(event: GameObjectSpawned): Unit = {
    val gameObject = event.getGameObject

    val id = gameObject.getId

//    if (RESOURCE_IDS.contains(id)) resources.add(new Resource(gameObject, skillIconManager, config.resourceIconSize))
//    else if (UTILITY_IDS.contains(id)) utilities.add(gameObject)
  }

  @Subscribe
  def onGameObjectDespawned(event: GameObjectDespawned): Unit = {
    val gameObject = event.getGameObject
    val id = gameObject.getId
//    if (RESOURCE_IDS.contains(gameObject.getId)) resources.removeIf((o: Resource) => o.getGameObject eq gameObject)
//    else if (UTILITY_IDS.contains(id)) utilities.remove(gameObject)
  }

  @Subscribe
  def onItemContainerChanged(event: ItemContainerChanged): Unit = {
    if (!inHunllef) return
    if (event.getContainerId == InventoryID.EQUIPMENT.getId) {
//      if (config.autoOffense) {
//        CombatUtils.togglePrayer(client, getPrayerBasedOnWeapon)
//      }
    }
  }


  def pluginEnabled(): Unit = {
    if (isGauntletVarbitSet) {
//      overlayTimer.setGauntletStart()
//      resourceManager.init()
      addSpawnedEntities()
      initGauntlet()
    }
    if (isHunllefVarbitSet) initHunllef()
  }

  private def addSpawnedEntities(): Unit = {
    import scala.jdk.CollectionConverters.CollectionHasAsScala
//    val op = (_: GameObjectSpawned).tap(_.setTile(null)).tap
    val op = new GameObjectSpawned().pipe {gos =>
      gos.setTile(null)
      gos.setGameObject
      .andThen(_ => gos)
    }.andThen(onGameObjectSpawned)

    val op2 = (new NpcSpawned(_)).andThen(onNpcSpawned)

    new GameObjectQuery().result(client).asScala.toList.foreach(op)
    client.getNpcs.asScala.toList.foreach(op2)
  }

  override def startUp(): Unit = {
    if (client.getGameState == GameState.LOGGED_IN) {
      //      val tpe = Demiboss.DemibossType$.MODULE$.fromId(NpcID.CRYSTALLINE_DRAGON).getOrElse(null)
      //      log.debug("Demiboss.fromId({}) = {}", NpcID.CRYSTALLINE_DRAGON, tpe)
      MessageUtils.addMessage(getName + " StartUp")
    }
    if(overlays.isEmpty) {
      overlays.add(overlayGauntlet)
    }
  }
  override protected def shutDown(): Unit = {
    if (client.getGameState == GameState.LOGGED_IN) {
      //      val tpe = Demiboss.DemibossType$.MODULE$.fromId(NpcID.CRYSTALLINE_DRAGON).getOrElse(null)
      //      log.debug("Demiboss.fromId({}) = {}", NpcID.CRYSTALLINE_DRAGON, tpe)
      MessageUtils.addMessage(getName + " ShutDown")
    }
  }

  def initHunllef(): Unit = {
    inHunllef = true

//    overlayTimer.setHunllefStart()
//    resourceManager.reset()

    overlayManager.remove(overlayGauntlet)
//    overlayManager.add(overlayHunllef)
//    overlayManager.add(overlayPrayerWidget)
//    overlayManager.add(overlayPrayerBox)
  }

  private def initGauntlet(): Unit = {
    inGauntlet = true
//    overlayManager.add(overlayTimer)
    overlayManager.add(overlayGauntlet)
  }


  def isGauntletVarbitSet: Boolean = client.getVarbitValue(9178) == 1

  def isHunllefVarbitSet: Boolean = {
    client.getVarbitValue(9177) == 1
  }

  @Subscribe
  def onVarbitChanged(event: VarbitChanged): Unit = {
    if (isHunllefVarbitSet) if (!inHunllef) initHunllef()
    else if (isGauntletVarbitSet) if (!inGauntlet) initGauntlet()
    else if (inGauntlet || inHunllef) shutDown()
  }


  @Subscribe
  def onAnimationChanged(event: AnimationChanged): Unit = {
    if(state.isEmpty) {
      return
    }
    (
      AnimationType.fromId(event.getActor.getAnimation).flatMap {
        case hunllefAnim: (HunllefAnimation & AttackAnimation) => {
          Some((_: HunllefState).updateHunllefAttackStyle(hunllefAnim.style))
        }
        case hunllefAnim: (AnimationType & HunllefAnimation) => {
          Some((_: HunllefState).updateAttack(false))
        }
        case playerAnim: (PlayerAnimation & AttackAnimation) => {
          val bossOpt = npcs.find(NpcType.Boss.ids.contains.compose[NPC](_.getId)(_))
          val validAttack = bossOpt.map(_.getComposition.getOverheadIcon).flatMap {
            case HeadIcon.MELEE => Some(AnimationType.Melee_Attack)
            case HeadIcon.RANGED => Some(AnimationType.Bow_Attack)
            case HeadIcon.MAGIC => Some(AnimationType.Magic_Attack)
            case _ => None
          }.exists(_ != playerAnim)
          Option((_: HunllefState).updateAttack(true)).filter(_ => validAttack)
        }
        case _ => None
      }).foreach(op => {
      state = state.map(op)
    })
  }
}

object GauntletPluginV3:

end GauntletPluginV3
