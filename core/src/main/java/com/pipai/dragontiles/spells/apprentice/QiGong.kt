package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class QiGong : StandardSpell() {
    override val id: String = "base:spells:QiGong"
    override val requirement: ComponentRequirement = Sequential(3)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(12),
        ScryAspect(2),
        TakeAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
