package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.SpellType

class CursedAura(amount: Int) : Status(amount) {
    override val id = "base:status:CursedAura"
    override val assetName = "cursed_aura.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return CursedAura(amount)
    }

    @CombatSubscribe
    suspend fun onSpellCasted(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.EFFECT) {
            val tiles: MutableList<Tile> = mutableListOf()
            repeat(amount) {
                tiles.add(Tile.FumbleTile())
            }
            api.addTilesToHand(tiles, TileStatus.CURSE)
        }
    }
}