package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.spells.upgrades.AntifreezeUpgrade

class Normalize : StandardSpell() {
    override val id: String = "base:spells:Normalize"
    override val type: SpellType = SpellType.EFFECT
    override val rarity: Rarity = Rarity.RARE
    override val targetType: TargetType = TargetType.NONE
    override val requirement: ComponentRequirement = Single(SuitGroup.STAR)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3),
        TransformAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val antifreeze = getUpgrades().any { u -> u is AntifreezeUpgrade }
        api.combat.hand.filter {
            it.tileStatus in listOf(TileStatus.BURN, TileStatus.FREEZE, TileStatus.SHOCK)
                    && (antifreeze || it.tileStatus != TileStatus.FREEZE)
        }
            .forEach {
                val targetSuit = when (it.tileStatus) {
                    TileStatus.BURN -> Suit.FIRE
                    TileStatus.FREEZE -> Suit.ICE
                    TileStatus.SHOCK -> Suit.LIGHTNING
                    else -> throw IllegalStateException("Unexpected tile status")
                }
                val number = when (val tile = it.tile) {
                    is Tile.ElementalTile -> tile.number
                    else -> api.runData.seed.miscRng().nextInt(1, 10)
                }
                api.transformTile(it, Tile.ElementalTile(targetSuit, number), false)
            }
        api.sortHand()
    }

}
