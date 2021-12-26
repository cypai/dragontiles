package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.Rarity

class StarCrystal : Potion() {
    override val id: String = "base:potions:StarCrystal"
    override val assetName: String = "star_crystal.png"
    override val rarity: Rarity = Rarity.COMMON
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        val tileOptions: MutableList<Tile> = mutableListOf(
            Tile.StarTile(StarType.EARTH),
            Tile.StarTile(StarType.MOON),
            Tile.StarTile(StarType.SUN),
            Tile.StarTile(StarType.STAR),
        )
        val result = api.queryTileOptions("Choose a tile to put in your hand", null, tileOptions, 1, 1)
        api.addTilesToHand(result, TileStatus.NONE)
    }
}
