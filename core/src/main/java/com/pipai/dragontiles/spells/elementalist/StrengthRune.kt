package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.getStackableAmount

class StrengthRune : Rune() {
    override val id: String = "base:spells:StrengthRune"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX()
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Strength(1), 1),
        XAspect(1, -1)
    )

    override suspend fun onActivate(api: CombatApi) {
        val amount = x() * aspects.getStackableAmount(Strength::class)
        if (amount > 0) {
            api.addStatusToHero(Strength(amount))
        }
    }

    override suspend fun onDeactivate(api: CombatApi) {
        val amount = x() * aspects.getStackableAmount(Strength::class)
        if (amount > 0) {
            api.addStatusToHero(Strength(-amount))
        }
    }
}
