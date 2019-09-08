package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy

interface Spell {
    val name: String
    val description: String
    val requirement: ComponentRequirement
    val targetType: TargetType

    fun createInstance(): SpellInstance
}

enum class TargetType {
    SINGLE, AOE
}

abstract class SpellInstance(
        val spell: Spell,
        var repeatable: Boolean,
        var repeatableMax: Int) {

    var repeated = 0
    var exhausted = false

    val componentSlots: List<ComponentSlot> = generateSlots(spell.requirement.slotAmount)

    open fun cast(targets: List<Enemy>, api: CombatApi) {
    }

    open fun onTurnStart(api: CombatApi) {
    }

    fun fill(components: List<Tile>) {
        componentSlots.zip(components) { slot, tile ->
            slot.tile = tile
        }
    }

    fun components() = componentSlots.filter { it.tile != null }.map { it.tile!! }.toList()
}

data class ComponentSlot(var tile: Tile?)

interface ComponentRequirement {
    val slotAmount: Int

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

    override val slotAmount = 1

    override fun find(hand: List<Tile>): List<List<Tile>> {
        return hand
                .filter { it.suit in allowedSuits }
                .map { listOf(it) }
    }
}
