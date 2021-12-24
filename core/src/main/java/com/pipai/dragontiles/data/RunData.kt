package com.pipai.dragontiles.data

import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.dungeon.DungeonMap

data class RunData(
    val hero: Hero,
    var dungeonMap: DungeonMap,
    val availableRelics: MutableList<String>,
    var town: Town?,
    var sideboardSpaceBought: Int,
    var potionChance: Float,
    val history: RunHistory,
    val seed: Seed,
) {

    fun adjustPostCombat(combat: Combat) {
        hero.flux = 0
        // TODO: history here
    }
}
