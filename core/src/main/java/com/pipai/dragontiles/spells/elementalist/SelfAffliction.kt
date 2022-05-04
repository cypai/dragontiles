package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class SelfAffliction : StandardSpell() {
    override val id: String = "base:spells:SelfAffliction"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3),
        LimitedRepeatableAspect(2),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tileStatus = when (elemental(components())) {
            Element.FIRE -> TileStatus.BURN
            Element.ICE -> TileStatus.FREEZE
            Element.LIGHTNING -> TileStatus.SHOCK
            else -> {
                throw IllegalStateException("Unexpected element")
            }
        }
        val hand = api.getHandTiles()
        if (hand.size > 1) {
            val tile = api.queryTiles("Pick a tile to ${tileStatus.toString().lowercase()}", hand, 1, 1)
            api.setTileStatus(tile, tileStatus)
        } else {
            api.setTileStatus(hand, tileStatus)
        }
    }
}
