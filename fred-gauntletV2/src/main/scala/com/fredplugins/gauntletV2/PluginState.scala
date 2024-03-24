package com.fredplugins.gauntletV2

import net.runelite.api.{ChatMessageType, Client, GameState, NPC}
import net.runelite.api.events.{GameTick, NpcDespawned, NpcSpawned}
import net.runelite.client.eventbus.{EventBus, Subscribe}

import scala.util.chaining.*
trait PluginState(val client: Client) {
  def onNpcSpawned(npcSpawned: NpcSpawned): Unit
  def onNpcDespawned(npcDespawned: NpcDespawned): Unit
  def onGameTick(event: GameTick): Unit
}
object PluginState {
  def create(c: Client): PluginState = {
    new PluginState(c) {
      val commonNpcs: collection.mutable.ListBuffer[NPC] = scala.collection.mutable.ListBuffer.empty[NPC]
      val tornados: collection.mutable.ListBuffer[Tornado] = scala.collection.mutable.ListBuffer.empty[Tornado]
      var hunllef: Option[Hunllef] = None

      @Subscribe
      override def onNpcSpawned(event: NpcSpawned): Unit = {
        val npc = event.getNpc
        NpcType.fromId(npc.getId) match {
          case Some(NpcType.Tornado) => {
            tornados.addOne(Tornado(npc))
          }
          case Some(NpcType.Boss) if (hunllef.isEmpty) => {
            hunllef = Option(new Hunllef(npc))
          }
          case Some(NpcType.Boss) if (hunllef.isDefined) => {}
          case Some(tpe) => {
            commonNpcs.addOne(npc)
          }
          case None =>
        }
      }

      @Subscribe
      override def onNpcDespawned(event: NpcDespawned): Unit = {
        val npc = event.getNpc
        NpcType.fromId(npc.getId) match {
          case Some(NpcType.Tornado) => {
            tornados.filterInPlace(t => t.getWrapped.ne(npc))
          }
          case Some(NpcType.Boss) if (hunllef.exists(h => h.getWrapped.eq(npc))) => {
            hunllef = Option.empty[Hunllef]
          }
          case Some(NpcType.Boss) => {}
          case Some(tpe) => {
            commonNpcs.filterInPlace(c => c != npc)
          }
          case None =>
        }
      }

      @Subscribe
      override def onGameTick(event: GameTick): Unit = {
        tornados.foreach(t => t.Control.tick())
        hunllef.foreach(h => {
          h.Control.decrementTicksUntilNextAttack()
        })

        if (client.getGameState == GameState.LOGGED_IN) {
          client.addChatMessage(ChatMessageType.GAMEMESSAGE, s"gauntletV2", s"GameTick(${client.getTickCount})", null);
          toString.split('\n').foreach(s => {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, s"gauntletV2", s, null);
          })
        }
      }

      override def toString: String = {
        val commonNpcStr = commonNpcs.toList.groupBy(npc => {
          NpcType.fromId(npc.getId)
        }).flatMap{
          case (Some(maybeType), value) => Some(maybeType -> value)
          case (None, _) => None
        }.toList.sortBy(_._1.ordinal).map{
          case (npcType, value) => s"${npcType} => ${value.mkString("[", ", ", "]")}"
        }.mkString("\n")

        val tornadoesStr = tornados.toList.map(t => s"Tornado(${t.getTimeRemaining})(${t.getWrapped.toString}").mkString("\n")

        val bossStr = hunllef.map(h => {
          s"Hunllef[${h.getAttackPhase}](${h.getAttackCount}/${h.getPlayerAttackCount}, ${h.getTicksUntilNextAttack}) = ${h.getWrapped}"
        }).getOrElse(s"null")

        s"${commonNpcStr}\n${tornadoesStr}\n${bossStr}"
      }
    }
  }
}
