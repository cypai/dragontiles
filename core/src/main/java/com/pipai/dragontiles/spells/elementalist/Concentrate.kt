package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.findAsWhere

class Concentrate : PowerSpell() {
    override val id: String = "base:spells:Concentrate"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ARCANE)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Strength(2), 1)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val stackable = aspects.findAsWhere(StackableAspect::class) { it.status is Strength }!!
        api.addStatusToHero(stackable.status.deepCopy())
    }
}
