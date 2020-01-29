package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.spells.common.Invoke

class Spark(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Spark"
    override val requirement: ComponentRequirement = SinglePredicate(
            { it.tile.let { t -> t is Tile.ElementalTile && t.number == 1 } },
            SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = Int.MAX_VALUE

    override fun baseDamage(): Int {
        return if (upgraded) {
            4
        } else {
            2
        }
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }

    override fun newClone(upgraded: Boolean): Invoke {
        return Invoke(upgraded)
    }
}
