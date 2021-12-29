package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.SpellType

class MenacingAura(amount: Int) : Status(amount) {
    override val id = "base:status:MenacingAura"
    override val assetName = "menacing_aura.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return MenacingAura(amount)
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
