package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class QuickInvoke(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:QuickInvoke"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = if (upgraded) 3 else 2

    override fun baseDamage(): Int = 3

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }

    override fun newClone(upgraded: Boolean): QuickInvoke {
        return QuickInvoke(upgraded)
    }
}
