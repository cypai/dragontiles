package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.findAsWhere

class ChannelStrength : PowerSpell() {
    override val id: String = "base:spells:ChannelStrength"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ANY_NO_FUMBLE)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Strength(2), 1),
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val stackable = aspects.findAsWhere(StackableAspect::class) { it.status is Strength }!!
        api.addStatusToHero(stackable.status.deepCopy())
    }
}
