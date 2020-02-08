package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Ground(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Ground"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = if (upgraded) 4 else 3

    override fun baseDamage(): Int = 0

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override fun newClone(upgraded: Boolean): Ground {
        return Ground(upgraded)
    }
}
