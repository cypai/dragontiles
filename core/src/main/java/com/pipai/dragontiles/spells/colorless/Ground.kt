package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Ground : StandardSpell() {
    override val strId: String = "base:spells:Ground"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        LimitedRepeatableAspect(2), FluxGainAspect(1)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
