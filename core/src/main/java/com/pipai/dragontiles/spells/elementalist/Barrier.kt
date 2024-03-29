package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Keywords
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Resistance

class Barrier : StandardSpell() {
    override val id: String = "base:spells:Barrier"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val glowType: GlowType = GlowType.ELEMENTED
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
        ExhaustAspect(),
    )

    override fun additionalKeywords(): List<String> {
        return listOf(Keywords.COMPONENT)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(Resistance(1, elemental(components()), true))
    }
}
