package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength

class StrengthRune : Rune() {
    override val id: String = "base:spells:StrengthRune"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX()
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        XAspect(-1)
    )

    override suspend fun onActivate(api: CombatApi) {
        val amount = x()
        if (amount > 0) {
            api.addStatusToHero(Strength(amount))
        }
    }

    override suspend fun onDeactivate(api: CombatApi) {
        val amount = x()
        if (amount > 0) {
            api.addStatusToHero(Strength(-components().size))
        }
    }
}
