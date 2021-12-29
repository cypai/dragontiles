package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Immunized
import com.pipai.dragontiles.utils.getStackableAmount

class PhoenixTears : StandardSpell() {
    override val id: String = "base:spells:PhoenixTears"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.LIFE)
    override val rarity: Rarity = Rarity.RARE
    override val targetType: TargetType = TargetType.NONE
    override val type: SpellType = SpellType.EFFECT
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Immunized(1), 1),
        FluxGainAspect(3),
        ExhaustAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(Immunized(aspects.getStackableAmount(Immunized::class)))
    }
}
