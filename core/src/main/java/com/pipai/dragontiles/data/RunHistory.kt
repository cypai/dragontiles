package com.pipai.dragontiles.data

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.spells.SpellUpgradeInstance

data class RunHistory(
    val victoryStatus: VictoryStatus,
    val trial: Int,
    val history: MutableList<FloorHistory>,
)

enum class VictoryStatus {
    IN_PROGRESS, ABANDONED, FAILED, VICTORY
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class FloorHistory {
    data class CombatFloorHistory(
        val dungeonId: String,
        val floorNumber: Int,
        val encounterId: String,
        val changeHistory: ChangeHistory?
    ) : FloorHistory()

    data class EliteFloorHistory(
        val dungeonId: String,
        val floorNumber: Int,
        val encounterId: String,
        val changeHistory: ChangeHistory?
    ) : FloorHistory()

    data class EventFloorHistory(
        val dungeonId: String,
        val floorNumber: Int,
        val eventId: String,
        val changeHistory: ChangeHistory?
    ) : FloorHistory()

    data class TownFloorHistory(
        val dungeonId: String,
        val floorNumber: Int,
        val innUsed: Boolean,
        val solicited: Boolean,
        val eventId: String?,
        val spellShop: SpellShop,
        val itemShop: ItemShop,
        val scribe: Scribe,
        val changeHistory: ChangeHistory?
    ) : FloorHistory()
}

data class ChangeHistory(
    var hp: Int,
    var hpMax: Int,
    var fluxMax: Int,
    var gold: Int,
    val spellsGained: MutableList<SpellInstance>,
    val spellsLost: MutableList<SpellInstance>,
    val spellsUpgraded: MutableList<Pair<SpellInstance, SpellUpgradeInstance>>,
    val relicsGained: MutableList<String>,
    val relicsLost: MutableList<String>,
    val potionsGained: MutableList<String>,
    val potionsLost: MutableList<String>,
)
