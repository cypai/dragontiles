package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.data.GlobalApi

abstract class Potion : Localized {
    abstract val assetName: String
    abstract val type: PotionType
    abstract val targetType: PotionTargetType

    abstract fun onNonCombatUse(api: GlobalApi)
    abstract suspend fun onCombatUse(target: Int?, api: CombatApi)
}

enum class PotionType {
    COMBAT_ONLY, UNIVERSAL
}

enum class PotionTargetType {
    ENEMY, NONE
}
