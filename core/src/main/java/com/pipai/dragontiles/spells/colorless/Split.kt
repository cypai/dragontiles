package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class Split : StandardSpell() {
    override val id: String = "base:spells:Split"
    override val requirement: ComponentRequirement = ForbidTransformFreeze(
        this,
        SinglePredicate({ (it.tile as Tile.ElementalTile).number > 1 }, SuitGroup.ELEMENTAL)
    )
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(2),
        TransformAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tileInstance = components().first()
        api.destroyTile(tileInstance)
        val tile = tileInstance.tile as Tile.ElementalTile
        val newTiles: MutableList<Tile> = mutableListOf()
        repeat(2) {
            newTiles.add(Tile.ElementalTile(tile.suit, tile.number / 2))
        }
        api.addTilesToHand(newTiles, TileStatus.NONE)
    }

    override suspend fun handleComponents(api: CombatApi) {
        // Component destruction handled in onCast
    }
}
