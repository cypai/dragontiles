package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.data.ItemShop
import com.pipai.dragontiles.data.Scribe
import com.pipai.dragontiles.data.SpellShop
import com.pipai.dragontiles.dungeonevents.DungeonEvent
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell

data class RunHistory(
    var sideboardSpaceBought: Int,
    val history: MutableList<FloorHistory>,
)

sealed class FloorHistory {
    data class CombatFloorHistory(val combat: Combat, val changeHistory: ChangeHistory)
    data class EliteFloorHistory(val combat: Combat, val changeHistory: ChangeHistory)
    data class EventFloorHistory(val event: DungeonEvent, val changeHistory: ChangeHistory)
    data class TownFloorHistory(
        val innUsed: Boolean,
        val solicited: Boolean,
        val event: DungeonEvent?,
        val spellShop: SpellShop,
        val itemShop: ItemShop,
        val scribe: Scribe,
        val changeHistory: ChangeHistory
    )
}

data class ChangeHistory(
    var hp: Int,
    var hpMax: Int,
    var fluxMax: Int,
    var gold: Int,
    val spellsGained: MutableList<Spell>,
    val spellsLost: MutableList<Spell>,
    val relicsGained: MutableList<Relic>,
    val relicsLost: MutableList<Relic>,
)
