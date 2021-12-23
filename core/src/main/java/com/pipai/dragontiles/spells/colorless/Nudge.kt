package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.data.predecessor
import com.pipai.dragontiles.spells.*

class Nudge : StandardSpell() {
    override val strId: String = "base:spells:Nudge"
    override val requirement: ComponentRequirement = SinglePredicate({ it.tileStatus != TileStatus.FREEZE }, SuitGroup.ANY_NO_FUMBLE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override suspend fun handleComponents(api: CombatApi) {
        val tile = components().first()
        api.transformTile(tile, predecessor(tile.tile), true)
    }
}