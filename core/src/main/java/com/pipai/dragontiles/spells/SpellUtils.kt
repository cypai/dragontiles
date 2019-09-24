package com.pipai.dragontiles.spells

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.TileInstance

fun elemental(components: List<TileInstance>): Element {
    return when (components.firstOrNull()?.tile?.suit) {
        Suit.FIRE -> Element.FIRE
        Suit.ICE -> Element.ICE
        Suit.LIGHTNING -> Element.LIGHTNING
        else -> Element.NONE
    }
}
