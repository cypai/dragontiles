package com.pipai.dragontiles.data

import kotlin.random.Random

data class Seed(
    val baseSeed: Long = Random.nextLong(),
    private var dungeonSeed: Long = baseSeed,
    private var relicSeed: Long = baseSeed,
    private var rewardSeed: Long = baseSeed,
    private var miscSeed: Long = baseSeed,
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

    fun miscRng(): Random {
        miscSeed++
        return Random(miscSeed)
    }
}
