package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import kotlin.math.min

class Singularity : StandardSpell() {
    override val id: String = "base:spells:Singularity"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(2),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tileInstance = components().first()
        api.destroyTile(tileInstance)
        val tile = tileInstance.tile as Tile.ElementalTile
        val amount = min(tile.number, 3)
        val newTiles: MutableList<Tile> = mutableListOf()
        repeat(amount) {
            newTiles.add(Tile.ElementalTile(tile.suit, 1))
        }
        api.addTilesToHand(newTiles)
    }

    override suspend fun handleComponents(api: CombatApi) {
        // Component destruction handled in onCast
    }
}
