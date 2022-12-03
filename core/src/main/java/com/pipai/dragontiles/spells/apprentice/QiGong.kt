package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class QiGong : StandardSpell() {
    override val id: String = "base:spells:QiGong"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(5)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tiles = api.queryTiles("Choose a tile to discard", api.getHandTiles(), 1, 1)
        if (tiles.isNotEmpty()) {
            api.discard(tiles)
        }
    }
}
