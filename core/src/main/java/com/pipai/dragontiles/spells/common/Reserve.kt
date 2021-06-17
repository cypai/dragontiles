package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Reserve : StandardSpell() {
    override val id: String = "base:spells:Reserve"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        LimitedRepeatableAspect(2)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override suspend fun handleComponents(api: CombatApi) {
        val tile = components().first()
        api.openDiscard(listOf(tile))
    }
}
