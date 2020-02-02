package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Mulligan(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Mulligan"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = if (upgraded) 2 else 1

    override fun baseDamage(): Int = 0

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.draw(1)
    }

    override fun newClone(upgraded: Boolean): Mulligan {
        return Mulligan(upgraded)
    }
}
