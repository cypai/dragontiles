package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.getLogger
import org.apache.commons.lang3.builder.ToStringBuilder

abstract class Spell(var upgraded: Boolean) {
    abstract val id: String
    abstract val requirement: ComponentRequirement
    abstract val targetType: TargetType

    abstract fun createInstance(): SpellInstance
}

enum class TargetType {
    SINGLE, AOE
}

abstract class SpellInstance(
        val spell: Spell,
        var repeatableMax: Int) {

    private val logger = getLogger()

    var repeated = 0
    var exhausted = false

    val componentSlots: List<ComponentSlot> = generateSlots(spell.requirement.slotAmount)

    fun available() = !exhausted && repeated < repeatableMax

    fun ready() = available() && spell.requirement.satisfied(componentSlots)

    fun cast(targets: List<Enemy>, api: CombatApi) {
        if (!ready()) {
            logger.error("Attempted to cast without being ready. State: $this")
            return
        }
        onCast(targets, api)
        handleComponents(api)
        repeated++
    }

    protected abstract fun onCast(targets: List<Enemy>, api: CombatApi)

    open fun handleComponents(api: CombatApi) {
        api.consume(components())
    }

    fun turnReset(api: CombatApi) {
        repeated = 0
        onTurnStart(api)
    }

    open fun onTurnStart(api: CombatApi) {
    }

    fun fill(components: List<Tile>) {
        componentSlots.zip(components) { slot, tile ->
            slot.tile = tile
        }
    }

    fun components() = componentSlots.filter { it.tile != null }.map { it.tile!! }.toList()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}

data class ComponentSlot(var tile: Tile?)

interface ComponentRequirement {
    val slotAmount: Int

    fun find(hand: List<Tile>): List<List<Tile>>

    fun satisfied(slots: List<ComponentSlot>): Boolean
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

    override fun satisfied(slots: List<ComponentSlot>): Boolean {
        return slots.size == 1
                && slots.firstOrNull()?.tile?.let { allowedSuits.contains(it.suit) } ?: false
    }
}
