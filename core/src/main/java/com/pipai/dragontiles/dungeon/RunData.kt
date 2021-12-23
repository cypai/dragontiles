package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.data.Town
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.relics.RelicData
import java.util.*

data class RunData(
    var dungeonMap: DungeonMap,
    val hero: Hero,
    val relicData: RelicData,
    var town: Town?,
    var sideboardSpaceBought: Int,
    var potionChance: Float,
    val history: RunHistory,
    val rng: Random = Random(),
)
