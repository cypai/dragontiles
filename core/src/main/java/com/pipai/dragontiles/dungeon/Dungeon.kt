package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.dungeonevents.DungeonEvent

data class Dungeon(
    override val id: String,
    val easyEncounters: List<Encounter>,
    val standardEncounters: List<Encounter>,
    val eliteEncounters: List<Encounter>,
    val bossEncounters: List<Encounter>,
    val dungeonEvents: List<DungeonEvent>,
    val startEvent: DungeonEvent,
) : Localized {

    fun getEncounter(id: String): Encounter? {
        return when (id) {
            in easyEncounters.map { it.id } -> {
                easyEncounters.first { it.id == id }
            }
            in standardEncounters.map { it.id } -> {
                standardEncounters.first { it.id == id }
            }
            in eliteEncounters.map { it.id } -> {
                eliteEncounters.first { it.id == id }
            }
            else -> {
                null
            }
        }
    }
}
