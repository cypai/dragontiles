package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile

interface Spell {
    val name: String
    val description: String
    val requirement: ComponentRequirement

    fun createInstance(): SpellInstance
}

abstract class SpellInstance(
        val spell: Spell,
        var repeatable: Boolean,
        var repeatableMax: Int) {

    var repeated = 0
    var exhausted = false

    open fun onCast(components: List<Tile>, api: CombatApi) {
    }

    open fun handleComponents(components: List<Tile>, api: CombatApi) {
        api.consume(components)
    }

    open fun onTurnStart(api: CombatApi) {
    }
}

data class ComponentSlot(var tile: Tile?)

interface ComponentRequirement {
    val componentSlots: List<ComponentSlot>

    fun find(hand: List<Tile>): List<List<Tile>>
}

private fun generateSlots(amount: Int): List<ComponentSlot> {
    val slots = mutableListOf<ComponentSlot>()
    repeat(amount) {
        slots.add(ComponentSlot(null))
    }
    return slots
}

class Single(private val allowedSuits: List<Suit>) : ComponentRequirement {
    constructor() : this(listOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR))

    override val componentSlots = generateSlots(1)

    override fun find(hand: List<Tile>): List<List<Tile>> {
        return hand
                .filter { it.suit in allowedSuits }
                .map { listOf(it) }
    }
}
