package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi

abstract class Potion {
    abstract val strId: String
    abstract val assetName: String
    abstract val targetType: PotionTargetType

    suspend fun use(target: Int?, api: CombatApi) {
        onUse(target, api)
        api.removePotion(this)
    }

    protected abstract suspend fun onUse(target: Int?, api: CombatApi)
}

enum class PotionTargetType {
    ENEMY, NONE
}
