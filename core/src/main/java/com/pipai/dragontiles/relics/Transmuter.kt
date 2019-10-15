package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.DrawPostEvent
import com.pipai.dragontiles.data.*

class Transmuter : Relic() {
    override val id = "base:Transmuter"

    @CombatSubscribe
    suspend fun postDraw(ev: DrawPostEvent, api: CombatApi) {
        val first = ev.tiles
                .map { it.first }
                .firstOrNull { it.tile is Tile.ElementalTile }
        val options = when (first?.tile?.suit) {
            Suit.FIRE -> fireTiles
            Suit.ICE -> iceTiles
            Suit.LIGHTNING -> lightningTiles
            else -> null
        }
        if (options != null) {
            val target = api.queryTransform(first!!, options)
            api.transformTile(first, target)
        }
    }
}
