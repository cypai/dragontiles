package com.pipai.dragontiles.spells

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.utils.findAs

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
        .maxByOrNull { it.number }!!.number
}

fun Spell.baseDamage(): Int {
    return aspects.findAs(AttackDamageAspect::class)!!.amount
}

fun Spell.baseFluxLoss(): Int {
    return aspects.findAs(FluxLossAspect::class)!!.amount
}

val anySet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR)
val elementalSet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING)
val arcaneSet = setOf(Suit.LIFE, Suit.STAR)
