package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import kotlin.math.min

class Singularity(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:Singularity"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val tileInstance = components().first()
        api.destroyTile(tileInstance)
        val tile = tileInstance.tile as Tile.ElementalTile
        val amount = if (upgraded) tile.number else min(tile.number, 3)
        val newTiles: MutableList<Tile> = mutableListOf()
        repeat(amount) {
            newTiles.add(Tile.ElementalTile(tile.suit, 1))
        }
        api.addTilesToHand(newTiles)
    }

    override suspend fun handleComponents(api: CombatApi) {
        // Component destruction handled in onCast
    }

    override fun newClone(upgraded: Boolean): Singularity {
        return Singularity(upgraded)
    }
}
