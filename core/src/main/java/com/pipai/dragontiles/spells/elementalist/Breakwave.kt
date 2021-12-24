package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getStackableAmount

class Breakwave : StandardSpell() {
    override val id: String = "base:spells:Breakwave"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ANY_NO_FUMBLE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(BreakStatus(5, false), 1),
        StackableAspect(Weak(5, false), 2),
        FluxGainAspect(5),
        ExhaustAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addAoeStatus(BreakStatus(aspects.getStackableAmount(BreakStatus::class), false))
        api.addAoeStatus(Weak(aspects.getStackableAmount(Weak::class), false))
    }
}
