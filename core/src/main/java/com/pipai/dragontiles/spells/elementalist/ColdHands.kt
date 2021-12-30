package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getStackableCopy

class ColdHands : StandardSpell() {
    override val id: String = "base:spells:ColdHands"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ICE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE_ENEMY
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Weak(3, false), 1),
        FluxGainAspect(1)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToEnemy(api.getEnemy(params.targets.first()), aspects.getStackableCopy(Weak::class))
    }
}
