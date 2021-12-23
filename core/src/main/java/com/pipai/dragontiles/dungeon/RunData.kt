package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.data.Town
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.relics.RelicData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*

@Serializable
data class RunData(
    var dungeon: Dungeon,
    val hero: Hero,
    val relicData: RelicData,
    var town: Town?,
    var sideboardSpaceBought: Int,
    var potionChance: Float,
    val history: RunHistory,
    @Transient val rng: Random = Random(),
)
