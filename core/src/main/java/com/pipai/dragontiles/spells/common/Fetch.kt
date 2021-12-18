package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Fetch : StandardSpell() {
    override val id: String = "base:spells:Fetch"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        if (api.combat.openPool.size < CombatApi.OPEN_POOL_SIZE) {
            api.drawToOpenPool(CombatApi.OPEN_POOL_SIZE - api.combat.openPool.size)
        }
    }
}