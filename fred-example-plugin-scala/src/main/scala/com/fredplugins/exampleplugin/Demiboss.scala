package com.fredplugins.exampleplugin

import net.runelite.api.{NPC, NpcID}

object Demiboss {
  enum DemibossType(val style: AttackStyle, val ids: Int*) {
    case Bear extends DemibossType(AttackStyle.Melee, NpcID.CRYSTALLINE_BEAR, NpcID.CORRUPTED_BEAR)
    case DarkBeast extends DemibossType(AttackStyle.Range, NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST)
    case Dragon extends DemibossType(AttackStyle.Magic, NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON)
  }
  object DemibossType {
    def fromId(id: Int): Option[DemibossType] = {
      DemibossType.values.find(dt => dt.ids.contains(id))
    }
  }
}
class Demiboss(val npc: NPC) {
}
