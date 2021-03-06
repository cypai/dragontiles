package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.*

class Transmuter : Relic() {
    override val id = "base:Transmuter"

    private var firstDraw: TileInstance? = null

    @CombatSubscribe
    fun handleDraw(ev: DrawEvent, api: CombatApi) {
        firstDraw = ev.tiles
                .map { it.first }
                .firstOrNull { it.tile is Tile.ElementalTile }
    }

    @CombatSubscribe
    fun handleTransform(ev: TileTransformedEvent, api: CombatApi) {
        if (firstDraw?.id == ev.previous.id) {
            firstDraw = ev.tile
        }
    }

    @CombatSubscribe(1)
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        val options = when (firstDraw?.tile?.suit) {
            Suit.FIRE -> fireTiles
            Suit.ICE -> iceTiles
            Suit.LIGHTNING -> lightningTiles
            else -> null
        }
        if (options != null) {
            val target = api.queryTransform(firstDraw!!, options)
            api.transformTile(firstDraw!!, target, true)
        }
    }

    @CombatSubscribe
    fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        firstDraw = null
    }
}
