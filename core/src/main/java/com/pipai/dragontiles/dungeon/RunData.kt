package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.data.Hero
import com.pipai.dragontiles.data.Town
import kotlin.random.Random

data class RunData(
    val hero: Hero,
    var dungeonMap: DungeonMap,
    val availableRelics: MutableList<String>,
    var town: Town?,
    var sideboardSpaceBought: Int,
    var potionChance: Float,
    val history: RunHistory,
    val seed: Seed,
)

data class Seed(
    val baseSeed: Long,
    private var dungeonSeed: Long = baseSeed,
    private var relicSeed: Long = baseSeed,
    private var rewardSeed: Long = baseSeed,
) {

    fun dungeonRng(): Random {
        dungeonSeed++
        return Random(dungeonSeed)
    }

    fun relicRng(): Random {
        relicSeed++
        return Random(relicSeed)
    }

    fun rewardRng(): Random {
        rewardSeed++
        return Random(rewardSeed)
    }
}
