package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Inflation : StandardSpell() {
    override val id: String = "base:spells:Inflation"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(10),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.changeTemporaryMaxFlux(api.runData.hero.tempFluxMax - api.runData.hero.fluxMax)
    }
}
