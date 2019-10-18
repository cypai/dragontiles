package com.pipai.dragontiles.spells

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance

fun elemental(components: List<TileInstance>): Element {
    return when (components.firstOrNull()?.tile?.suit) {
        Suit.FIRE -> Element.FIRE
        Suit.ICE -> Element.ICE
        Suit.LIGHTNING -> Element.LIGHTNING
        else -> Element.NONE
    }
}

fun numeric(components: List<TileInstance>): Int {
    return components
            .filter { it.tile is Tile.ElementalTile }
            .map { it.tile as Tile.ElementalTile }
            .maxBy { it.number }!!.number
}

val anySet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR)
val elementalSet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING)
val arcaneSet = setOf(Suit.LIFE, Suit.STAR)
