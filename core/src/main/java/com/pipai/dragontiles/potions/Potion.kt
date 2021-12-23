package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.dungeon.GlobalApi
import kotlinx.serialization.Serializable

@Serializable
abstract class Potion {
    abstract val strId: String
    abstract val assetName: String
    abstract val type: PotionType
    abstract val targetType: PotionTargetType

    fun useOutsideCombat(api: GlobalApi) {
        onNonCombatUse(api)
        api.removePotion(this)
    }

    suspend fun useDuringCombat(target: Int?, api: CombatApi) {
        onCombatUse(target, api)
        api.removePotion(this)
    }

    protected abstract fun onNonCombatUse(api: GlobalApi)
    protected abstract suspend fun onCombatUse(target: Int?, api: CombatApi)
}

enum class PotionType {
    COMBAT_ONLY, UNIVERSAL
}

enum class PotionTargetType {
    ENEMY, NONE
}
