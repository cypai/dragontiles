package com.pipai.dragontiles.combat

import com.pipai.dragontiles.relics.Relic

data class CombatRewards(
    val spellRewardType: SpellRewardType,
    val gold: Int,
    val randomRelic: Boolean,
    val relic: Relic?,
    val potionChance: Float,
)

enum class SpellRewardType {
    STANDARD, ELITE, BOSS
}
