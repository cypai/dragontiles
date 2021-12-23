package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Mulligan : StandardSpell() {
    override val strId: String = "base:spells:Mulligan"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.SPECIAL
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        LimitedRepeatableAspect(2)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.draw(1)
    }
}
