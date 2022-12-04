package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Scry : StandardSpell() {
    override val id: String = "base:spells:Scry"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        ScryAspect(null),
        FluxGainAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
