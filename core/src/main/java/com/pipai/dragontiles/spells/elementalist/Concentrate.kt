package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength

class Concentrate(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Concentrate"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): Concentrate {
        return Concentrate(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(Strength(2))
    }
}
