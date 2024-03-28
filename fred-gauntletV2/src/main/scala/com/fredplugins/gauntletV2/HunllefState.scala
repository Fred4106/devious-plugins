package com.fredplugins.gauntletV2

import net.runelite.api.{ChatMessageType, Client, GameState, NPC}
import net.runelite.api.events.{GameTick, NpcDespawned, NpcSpawned}
import net.runelite.client.eventbus.{EventBus, Subscribe}

import scala.util.chaining.*

case class HunllefState(tornadoTimeRemaining: Int, ticksUntilNextAttack: Int, attackCount: Int, playerAttackCount: Int, hunllefAttackPhase: AttackStyle) {}
object HunllefState {
  private val ATTACK_TICK_SPEED = 6
  private val MAX_ATTACK_COUNT = 4
  private val MAX_PLAYER_ATTACK_COUNT = 6

  def init(): HunllefState = {
    HunllefState(0, 0, MAX_ATTACK_COUNT, MAX_PLAYER_ATTACK_COUNT, AttackStyle.Range)
  }
  extension (s: HunllefState) {
    def gameTick(): HunllefState = {
      s.copy(
        tornadoTimeRemaining = math.max(0, s.tornadoTimeRemaining - 1),
        ticksUntilNextAttack = math.max(0, s.ticksUntilNextAttack - 1)
      )
    }
    def spawnTornado(): HunllefState = {
      s.copy(tornadoTimeRemaining = 21)
    }
    def updateAttack(player: Boolean): HunllefState = {
      s.copy(
        ticksUntilNextAttack = if(!player) ATTACK_TICK_SPEED else s.ticksUntilNextAttack,
        playerAttackCount = {
          if (player) (s.playerAttackCount - 1).pipe(pac => if (pac <= 0) MAX_PLAYER_ATTACK_COUNT else pac)
          else s.playerAttackCount
        },
        attackCount = {
          if (!player) (s.attackCount - 1).pipe(pac => if(pac <= 0) MAX_ATTACK_COUNT else pac)
          else s.attackCount
        }
      )
    }
    def updateHunllefAttackStyle(nAttackStyle: AttackStyle): HunllefState = {
      s.copy(hunllefAttackPhase = nAttackStyle)
    }
  }
}
