package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.relics.Relic

data class CombatRewardConfig(
    val spellRewardType: SpellRewardType,
    val gold: Int,
    val randomRelic: Boolean,
    val relic: Relic?,
    val potionChance: Float,
) {

    companion object {

        fun standard(runData: RunData): CombatRewardConfig {
            return CombatRewardConfig(SpellRewardType.STANDARD, 3, false, null, runData.potionChance)
        }

        fun elite(runData: RunData): CombatRewardConfig {
            return CombatRewardConfig(SpellRewardType.ELITE, 5, false, null, runData.potionChance + 0.2f)
        }
    }
}

enum class SpellRewardType {
    STANDARD, ELITE, BOSS
}
