package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class DumpFlux : StandardSpell() {
    override val id: String = "base:spells:DumpFlux"
    override val requirement: ComponentRequirement = Identical(4)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(5),
        FluxLossAspect(40),
        ExhaustAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
