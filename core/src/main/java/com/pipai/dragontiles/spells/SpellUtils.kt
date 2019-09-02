package com.pipai.dragontiles.spells

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile

fun elemental(components: List<Tile>): Element {
    return when (components.firstOrNull()?.suit) {
        Suit.FIRE -> Element.FIRE
        Suit.ICE -> Element.ICE
        Suit.LIGHTNING -> Element.LIGHTNING
        else -> Element.NONE
    }
}
