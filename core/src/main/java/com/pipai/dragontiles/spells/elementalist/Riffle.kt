package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Riffle : StandardSpell() {
    override val id: String = "base:spells:Riffle"
    override val requirement: ComponentRequirement = Sequential(2, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        SwapAspect(1),
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
