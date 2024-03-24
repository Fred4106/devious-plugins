package com.fredplugins.gauntletV2

import net.runelite.api.{NPC, NpcID, NullNpcID}

enum AttackStyle {
  case Melee extends AttackStyle
  case Range extends AttackStyle
  case Magic extends AttackStyle
}

sealed trait DemibossType(val attackStyle: AttackStyle) {
  self: NpcType =>
}
enum NpcType(val ids: Int*) {
  case Weak extends NpcType(NpcID.CRYSTALLINE_BAT, NpcID.CORRUPTED_BAT, NpcID.CRYSTALLINE_RAT, NpcID.CORRUPTED_RAT, NpcID.CRYSTALLINE_SPIDER, NpcID.CORRUPTED_SPIDER)
  case Strong extends NpcType(NpcID.CRYSTALLINE_SCORPION, NpcID.CORRUPTED_SCORPION, NpcID.CRYSTALLINE_UNICORN, NpcID.CORRUPTED_UNICORN, NpcID.CRYSTALLINE_WOLF, NpcID.CORRUPTED_WOLF)
  case Bear extends NpcType(NpcID.CRYSTALLINE_BEAR, NpcID.CORRUPTED_BEAR) with DemibossType(AttackStyle.Melee)
  case DarkBeast extends NpcType(NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST) with DemibossType(AttackStyle.Range)
  case Dragon extends NpcType(NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON) with DemibossType(AttackStyle.Magic)
  case Boss extends NpcType(NpcID.CRYSTALLINE_HUNLLEF, NpcID.CRYSTALLINE_HUNLLEF_9022, NpcID.CRYSTALLINE_HUNLLEF_9023, NpcID.CRYSTALLINE_HUNLLEF_9024, NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF_9036, NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9038)
  case Tornado extends NpcType(NullNpcID.NULL_9025, NullNpcID.NULL_9039)
}
object NpcType {
  def fromId(id: Int): Option[NpcType] = {
    NpcType.values.find(dt => dt.ids.contains(id))
  }
}

class Tornado(w: NPC) {
  private var timeLeft = 21;

  def getWrapped: NPC = w
  def getTimeRemaining: Int = timeLeft
  object Control {
    def tick(): Unit = timeLeft -= (if (getTimeRemaining > 0) 1 else 0)
  }
}
object Hunllef {
  private val ATTACK_TICK_SPEED = 6
  private val MAX_ATTACK_COUNT = 4
  private val MAX_PLAYER_ATTACK_COUNT = 6
}
class Hunllef(w: NPC) {
  private var attackPhase: AttackStyle = AttackStyle.Range
  private var attackCount: Int = Hunllef.MAX_ATTACK_COUNT
  private var playerAttackCount: Int = Hunllef.MAX_PLAYER_ATTACK_COUNT
  private var ticksUntilNextAttack: Int = 0

  def getWrapped: NPC = w

  def getAttackPhase: AttackStyle = attackPhase
  def getAttackCount: Int = attackCount
  def getPlayerAttackCount: Int = playerAttackCount
  def getTicksUntilNextAttack: Int = ticksUntilNextAttack

  object Control {
    def decrementTicksUntilNextAttack(): Unit = {
      if (getTicksUntilNextAttack > 0) ticksUntilNextAttack -= 1
    }

    def updatePlayerAttackCount(): Unit = {
      playerAttackCount =
        if (playerAttackCount > 1) playerAttackCount - 1
        else Hunllef.MAX_PLAYER_ATTACK_COUNT
    }

    def updateAttackCount(): Unit = {
      ticksUntilNextAttack = Hunllef.ATTACK_TICK_SPEED
      attackCount =
        if (attackCount > 1) attackCount - 1
        else Hunllef.MAX_ATTACK_COUNT
    }

    def toggleAttackHunllefAttackStyle(): Unit = {
      import AttackStyle.*
      attackPhase = attackPhase match {
        case Range => Magic
        case Magic => Range
        case Melee => ???
      }
    }
  }
}

