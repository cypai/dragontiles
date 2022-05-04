package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class Chillsink : StandardSpell() {
    override val id: String = "base:spells:Chillsink"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ICE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE_ENEMY
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(7, false),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val statuses =
            api.getHandTiles().filter { it.tileStatus in listOf(TileStatus.BURN, TileStatus.FREEZE, TileStatus.SHOCK) }
        api.setTileStatus(statuses, TileStatus.NONE)
        repeat(statuses.size) {
            api.heroLoseFlux(baseFluxLoss())
        }
    }
}
