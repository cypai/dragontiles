package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Strike(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Strike"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 7

    override fun newClone(upgraded: Boolean): Strike {
        return Strike(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage() + if (upgraded) 3 else 0)
    }
}
