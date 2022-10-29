package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getStackableAmount

class SecondWind : StandardSpell() {
    override val id: String = "base:spells:SecondWind"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(9),
        ExhaustAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.draw(api.runData.hero.handSize - api.numTilesInHand())
    }
}
