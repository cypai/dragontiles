package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class FluxCapacitor : StandardSpell() {
    override val id: String = "base:spells:FluxCapacitor"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        TempMaxFluxGainAspect(15)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.changeTemporaryMaxFlux(aspects.findAs(TempMaxFluxGainAspect::class)!!.amount)
    }
}
