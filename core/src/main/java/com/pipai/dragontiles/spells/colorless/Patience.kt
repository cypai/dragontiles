package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.NoDraw

class Patience : StandardSpell() {
    override val id: String = "base:spells:Patience"
    override val requirement: ComponentRequirement = Single(SuitGroup.ANY)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FetchAspect(null),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(NoDraw(2))
    }
}
