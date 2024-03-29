package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Battery : StandardSpell() {
    override val id: String = "base:spells:Battery"
    override val requirement: ComponentRequirement = Single(SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        TempMaxFluxChangeAspect(4)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
