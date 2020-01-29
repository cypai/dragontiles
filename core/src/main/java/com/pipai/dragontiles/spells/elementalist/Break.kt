package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class Break(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Break"
    override val requirement: ComponentRequirement = Identical(3)
    override val targetType: TargetType = TargetType.SINGLE_ENEMY

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): Break {
        return Break(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val status = when (elemental(components())) {
            Element.FIRE -> Status.FIRE_BREAK
            Element.ICE -> Status.ICE_BREAK
            Element.LIGHTNING -> Status.LIGHTNING_BREAK
            Element.NONE -> Status.NONELEMENTAL_BREAK
        }
        api.changeEnemyStatusIncrement(params.targets.first(), status, if (upgraded) 4 else 3)
    }
}
