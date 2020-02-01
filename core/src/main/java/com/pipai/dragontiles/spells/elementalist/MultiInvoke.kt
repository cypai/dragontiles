package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class MultiInvoke(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:MultiInvoke"
    override val requirement: ComponentRequirement = SequentialX()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 1

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        val x = components().size + if (upgraded) 1 else 0
        repeat(x) {
            api.attack(target, elemental(components()), baseDamage())
        }
    }

    override fun newClone(upgraded: Boolean): MultiInvoke {
        return MultiInvoke(upgraded)
    }
}
