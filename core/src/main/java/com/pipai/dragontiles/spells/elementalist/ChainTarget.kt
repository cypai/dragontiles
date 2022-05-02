package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Chain

class ChainTarget : StandardSpell() {
    override val id: String = "base:spells:ChainTarget"
    override val requirement: ComponentRequirement = Sequential(3)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(7),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.addStatusToEnemy(target, Chain(1, elemental(components())))
    }
}
