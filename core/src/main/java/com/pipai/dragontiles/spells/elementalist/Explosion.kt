package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Explosion(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Explosion"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON

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
                api.attack(api.getEnemy(it), elemental(components()), baseDamage())
            }
        }
    }
}
