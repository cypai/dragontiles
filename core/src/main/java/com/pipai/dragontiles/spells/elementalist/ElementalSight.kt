package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class ElementalSight : StandardSpell() {
    override val id: String = "base:spells:ElementalSight"
    override val requirement: ComponentRequirement = AnyCombo(1, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FetchAspect(4),
        FluxGainAspect(1),
        OpenDrawAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
