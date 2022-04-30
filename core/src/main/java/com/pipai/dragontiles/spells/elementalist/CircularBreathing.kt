package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class CircularBreathing : StandardSpell() {
    override val id: String = "base:spells:CircularBreathing"
    override val requirement: ComponentRequirement = AnyCombo(2, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(5),
        TempMaxFluxChangeAspect(5),
        DrawAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
