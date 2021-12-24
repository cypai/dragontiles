package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength

class FeedbackLoop : StandardSpell() {
    override val id: String = "base:spells:FeedbackLoop"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(5),
    )

    override fun additionalLocalized(): List<String> = listOf(Strength(0).id)

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(Strength(api.heroStatusAmount(Strength::class)))
    }
}
