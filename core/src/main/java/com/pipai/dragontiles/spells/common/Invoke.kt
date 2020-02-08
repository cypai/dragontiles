package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Invoke(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Invoke"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = if (upgraded) 2 else 1

    override fun baseDamage(): Int = 2

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }

    override fun newClone(upgraded: Boolean): Invoke {
        return Invoke(upgraded)
    }
}
