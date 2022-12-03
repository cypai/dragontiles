package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.findAsWhere

class QiGong : PowerSpell() {
    override val id: String = "base:spells:QiGong"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ANY_NO_FUMBLE)
    override val rarity: Rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(9)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tiles = api.queryTiles("Choose a tile to discard", api.getHandTiles(), 1, 1)
        if (tiles.isNotEmpty()) {
            api.discard(tiles)
        }
    }
}
