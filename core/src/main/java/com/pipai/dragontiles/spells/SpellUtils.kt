package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.status.Cryo
import com.pipai.dragontiles.status.Electro
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.findAs

fun elemental(components: List<TileInstance>): Element {
    return when (components.firstOrNull()?.tile?.suit) {
        Suit.FIRE -> Element.FIRE
        Suit.ICE -> Element.ICE
        Suit.LIGHTNING -> Element.LIGHTNING
        else -> Element.NONE
    }
}

fun reactantFlag(components: List<TileInstance>): CombatFlag? {
    return when (elemental(components)) {
        Element.FIRE -> CombatFlag.PYRO
        Element.ICE -> CombatFlag.CRYO
        Element.LIGHTNING -> CombatFlag.ELECTRO
        Element.NONE -> null
    }
}

fun reactant(components: List<TileInstance>): Status? {
    return when (elemental(components)) {
        Element.FIRE -> Pyro(1)
        Element.ICE -> Cryo(1)
        Element.LIGHTNING -> Electro(1)
        Element.NONE -> null
    }
}

fun numeric(components: List<TileInstance>): Int {
    return components
        .filter { it.tile is Tile.ElementalTile }
        .map { it.tile as Tile.ElementalTile }
        .maxByOrNull { it.number }!!.number
}

fun Spell.baseDamage(): Int {
    return aspects.findAs(AttackDamageAspect::class)?.amount ?: 0
}

fun Spell.baseFluxGain(): Int {
    return aspects.findAs(FluxGainAspect::class)?.amount ?: 0
}

fun Spell.baseFluxLoss(): Int {
    return aspects.findAs(FluxLossAspect::class)?.amount ?: 0
}

fun Spell.baseSwap(): Int {
    return aspects.findAs(SwapAspect::class)?.amount ?: 0
}

fun Spell.x(): Int {
    return aspects.findAs(XAspect::class)?.amount ?: 0
}

val anySet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR, Suit.FUMBLE)
val anyNoFumbleSet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR)
val elementalSet = setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING)
val arcaneSet = setOf(Suit.LIFE, Suit.STAR)
