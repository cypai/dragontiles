package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Blast(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Blast"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = if (upgraded) 6 else 5

    override fun newClone(upgraded: Boolean): Blast {
        return Blast(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        params.targets.forEach {
            api.attack(api.getEnemy(it), elemental(components()), baseDamage())
        }
    }
}
