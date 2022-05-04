package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getStackableAmount

class WindChill : StandardSpell() {
    override val id: String = "base:spells:WindChill"
    override val requirement: ComponentRequirement = Sequential(2, SuitGroup.ICE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Weak(1, false), 1),
        FluxGainAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addAoeStatus(Weak(aspects.getStackableAmount(Weak::class), false))
        val hand = api.getHandTiles()
        if (hand.size > 1) {
            val tile = api.queryTiles("Pick a tile to freeze", hand, 1, 1)
            api.setTileStatus(tile, TileStatus.FREEZE)
        } else {
            api.setTileStatus(hand, TileStatus.FREEZE)
        }
    }
}
