package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Explosion(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Explosion"
    override val requirement: ComponentRequirement = Identical(3)
    override val targetType: TargetType = TargetType.AOE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int {
        return if (upgraded) {
            3 * numeric(components())
        } else {
            2 * numeric(components())
        }
    }

    override fun newClone(upgraded: Boolean): Explosion {
        return Explosion(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        exhausted = true
        repeat(2) {
            params.targets.forEach {
                api.attack(api.getTargetable(it), elemental(components()), baseDamage())
            }
        }
    }
}
