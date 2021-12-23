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
) : Localized
