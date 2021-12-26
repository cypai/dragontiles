package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.Rarity

class LightningCrystal : Potion() {
    override val id: String = "base:potions:LightningCrystal"
    override val assetName: String = "lightning_crystal.png"
    override val rarity: Rarity = Rarity.COMMON
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        val tileOptions: MutableList<Tile> = mutableListOf()
        (1..9).forEach {
            tileOptions.add(Tile.ElementalTile(Suit.LIGHTNING, it))
        }
        val result = api.queryTileOptions("Choose a tile to put in your hand", null, tileOptions, 1, 1)
        api.addTilesToHand(result, TileStatus.NONE)
    }
}
