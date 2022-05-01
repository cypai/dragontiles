package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.getStackableCopy

class GreatPower : Sorcery() {
    override val id = "base:spells:GreatPower"
    override val requirement = Identical(3)
    override val rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Strength(2), 1),
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.addStatusToHero(aspects.getStackableCopy(Strength::class))
    }
}
