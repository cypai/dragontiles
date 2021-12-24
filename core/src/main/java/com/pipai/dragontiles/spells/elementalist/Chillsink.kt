package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getStackableCopy

class Chillsink : StandardSpell() {
    override val id: String = "base:spells:Chillsink"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ICE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE_ENEMY
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(5)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val freezes = api.combat.hand.filter { it.tileStatus == TileStatus.FREEZE }
        api.setTileStatus(freezes, TileStatus.NONE)
        repeat (freezes.size) {
            api.heroLoseFlux(baseFluxLoss())
        }
    }
}
