package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class FluxCapacitor : StandardSpell() {
    override val id: String = "base:spells:FluxCapacitor"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        TempMaxFluxChangeAspect(12)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
