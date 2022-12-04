package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Meditate : StandardSpell() {
    override val id: String = "base:spells:Meditate"
    override val requirement: ComponentRequirement = Single(SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(8),
        ScryAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
