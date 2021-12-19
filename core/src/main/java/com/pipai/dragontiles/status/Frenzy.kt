package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.SpellType

class Frenzy(amount: Int) : Status(amount) {
    override val strId = "base:status:Frenzy"
    override val assetName = "assets/binassets/graphics/status/frenzy.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Frenzy(amount)
    }

    @CombatSubscribe
    suspend fun onSpellCasted(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.EFFECT) {
            val tiles: MutableList<Tile> = mutableListOf()
            repeat(amount) {
                tiles.add(Tile.FumbleTile())
            }
            api.addTilesToHand(tiles, TileStatus.VOLATILE)
        }
    }
}
