package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Splash : StandardSpell() {
    override val id: String = "base:spells:Splash"
    override val requirement: ComponentRequirement = Single(SuitGroup.ICE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        LimitedRepeatableAspect(2), FluxGainAspect(1)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
