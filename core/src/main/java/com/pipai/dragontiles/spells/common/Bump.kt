package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.successor
import com.pipai.dragontiles.spells.*

class Bump(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Bump"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = if (upgraded) 2 else 1

    override fun baseDamage(): Int = 0

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override suspend fun handleComponents(api: CombatApi) {
        val tile = components().first()
        api.transformTile(tile, successor(tile.tile), true)
    }

    override fun newClone(upgraded: Boolean): Bump {
        return Bump(upgraded)
    }
}
