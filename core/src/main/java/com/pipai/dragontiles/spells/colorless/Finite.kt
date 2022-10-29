package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Finite : StandardSpell() {
    override val id: String = "base:spells:Finite"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
        CountdownAspect(44, CountdownType.NUMERIC_SCORE, this::cdCallback)
    )

    override fun additionalKeywords(): List<String> = listOf("@N")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    private suspend fun cdCallback(api: CombatApi) {
        api.score()
    }
}
