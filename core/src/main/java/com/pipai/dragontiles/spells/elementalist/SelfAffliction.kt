package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.combat.TileStatusInflictStrategy
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.getStackableCopy
import com.pipai.dragontiles.utils.withAll

class SelfAffliction : StandardSpell() {
    override val id: String = "base:spells:SelfAffliction"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
        LimitedRepeatableAspect(2),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.PYRO))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tileStatus = when (elemental(components())) {
            Element.FIRE -> TileStatus.BURN
            Element.ICE -> TileStatus.FREEZE
            Element.LIGHTNING -> TileStatus.SHOCK
            else -> {
                throw IllegalStateException("Unexpected element")
            }
        }
        if (api.combat.hand.isNotEmpty()) {
            if (api.combat.hand.size > 1) {
                val tile = api.queryTiles("Pick a tile to ${tileStatus.toString().lowercase()}", api.combat.hand, 1, 1)
                api.setTileStatus(tile, tileStatus)
            } else {
                api.setTileStatus(api.combat.hand, tileStatus)
            }
        }
    }
}
