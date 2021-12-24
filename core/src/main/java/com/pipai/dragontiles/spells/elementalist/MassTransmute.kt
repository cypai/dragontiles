package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.spells.upgrades.AntifreezeUpgrade

class MassTransmute : StandardSpell() {
    override val id: String = "base:spells:MassTransmute"
    override val type: SpellType = SpellType.EFFECT
    override val rarity: Rarity = Rarity.RARE
    override val targetType: TargetType = TargetType.NONE
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        ExhaustAspect(),
        FluxGainAspect(20),
        TransformAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val component = components().first().tile as Tile.ElementalTile
        val antifreeze = getUpgrades().any { u -> u is AntifreezeUpgrade }
        api.combat.hand.filter { it.tile is Tile.ElementalTile && (antifreeze || it.tileStatus != TileStatus.FREEZE) }
            .forEach {
                val tile = it.tile as Tile.ElementalTile
                api.transformTile(it, Tile.ElementalTile(component.suit, tile.number), false)
            }
        api.sortHand()
    }

}
