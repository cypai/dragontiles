package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withAll

class ExpandAnomaly : StandardSpell() {
    override val id: String = "base:spells:ExpandAnomaly"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val statuses = listOf(TileStatus.BURN, TileStatus.FREEZE, TileStatus.SHOCK)
        val tile = api.queryTiles(
            "Select a Burn, Freeze, or Shock tile",
            api.combat.hand.filter { it.tileStatus in statuses },
            0,
            1,
        ).firstOrNull()
        if (tile != null) {
            api.setTileStatus(
                api.combat.hand.filter { it.tile.suit == tile.tile.suit }
                    .withAll(api.combat.openPool.filter { it.tile.suit == tile.tile.suit }),
                tile.tileStatus
            )
            api.sortHand()
        }
    }
}
