package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class Denormalize : StandardSpell() {
    override val id: String = "base:spells:Denormalize"
    override val type: SpellType = SpellType.EFFECT
    override val rarity: Rarity = Rarity.UNCOMMON
    override val targetType: TargetType = TargetType.NONE
    override val requirement: ComponentRequirement = AnyX(SuitGroup.ANY) {
        it.tileStatus in listOf(TileStatus.BURN, TileStatus.FREEZE, TileStatus.SHOCK)
    }
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
        Antifreeze(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        components()
            .forEach {
                val targetSuit = when (it.tileStatus) {
                    TileStatus.BURN -> Suit.FIRE
                    TileStatus.FREEZE -> Suit.ICE
                    TileStatus.SHOCK -> Suit.LIGHTNING
                    else -> throw IllegalStateException("Unexpected tile status")
                }
                val number = when (val tile = it.tile) {
                    is Tile.ElementalTile -> tile.number
                    else -> api.rng.nextInt(1, 10)
                }
                api.transformTile(it, Tile.ElementalTile(targetSuit, number), false)
            }
        api.sortHand()
    }

    override suspend fun handleComponents(api: CombatApi) {
        // skip consume
    }

}
