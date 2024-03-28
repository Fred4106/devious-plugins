package com.fredplugins.gauntletV2

import com.fredplugins.gauntletV2.AttackStyle.Melee
import com.fredplugins.gauntletV2.AttackStyle.Range
import com.fredplugins.gauntletV2.AttackStyle.Magic
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

sealed trait AttackAnimation(val style: AttackStyle) {
  self: AnimationType =>
}

sealed trait HunllefAnimation {
  self: AnimationType =>
}

sealed trait PlayerAnimation {
  self: AnimationType =>
}

enum AnimationType(val ids: Int*) {
  case Melee_Attack extends AnimationType(422, 423, 390,386, 395, 401, 400, 428, 440) with AttackAnimation(Melee) with PlayerAnimation
  case Bow_Attack extends AnimationType(426) with AttackAnimation(Range) with PlayerAnimation
  case Magic_Attack extends AnimationType(1167) with AttackAnimation(Magic) with PlayerAnimation

  case Hunllef_Tornado extends AnimationType(8418) with HunllefAnimation
  case Hunllef_Attack_Unknown extends AnimationType(8419) with HunllefAnimation
  case Hunllef_Attack_Mage extends AnimationType(8754) with AttackAnimation(Magic) with HunllefAnimation
  case Hunllef_Attack_Range extends AnimationType(8755) with AttackAnimation(Range) with HunllefAnimation

  def isPlayer: Boolean = this match {
    case _: (AnimationType & PlayerAnimation) => true
    case _ => false
  }

  def isHunllef: Boolean = this match {
    case _: (AnimationType & HunllefAnimation) => true
    case _ => false
  }
}
object AnimationType {
  def fromId(id: Int): Option[AnimationType] = {
    AnimationType.values.find(dt => dt.ids.contains(id))
  }

//  private val attackAnim = List(Melee_Attack,
//    Bow_Attack,
//    Magic_Attack)
//  def isAttackAnim(id: Int): Boolean = {
//    fromId(id).exists(attackAnim.contains(_))
//  }
}