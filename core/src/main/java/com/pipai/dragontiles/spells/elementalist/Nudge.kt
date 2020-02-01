package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.predecessor
import com.pipai.dragontiles.spells.*

class Nudge(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Nudge"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = if (upgraded) 2 else 1

    override fun baseDamage(): Int = 0

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override suspend fun handleComponents(api: CombatApi) {
        val tile = components().first()
        api.transformTile(tile, predecessor(tile.tile))
    }

    override fun newClone(upgraded: Boolean): Nudge {
        return Nudge(upgraded)
    }
}
