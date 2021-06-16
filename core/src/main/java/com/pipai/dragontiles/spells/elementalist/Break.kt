package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus

class Break(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Break"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE_ENEMY
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): Break {
        return Break(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToEnemy(api.getEnemy(params.targets.first()), BreakStatus(3, false))
    }
}
