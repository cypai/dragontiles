package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.TileInstance
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

    abstract val baseDamage: Int
    var repeated = 0
    var exhausted = false

    val componentSlots: List<ComponentSlot> = generateSlots(spell.requirement.slotAmount)

    val data: MutableMap<String, Int> = mutableMapOf()

    fun dynamicValue(key: String): Int {
        return when (key) {
            "!r" -> repeatableMax - repeated
            "!d" -> baseDamage
            else -> data[key] ?: 0
        }
    }

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

    fun fill(components: List<TileInstance>) {
        componentSlots.zip(components) { slot, tile ->
            slot.tile = tile
        }
    }

    fun components() = componentSlots.filter { it.tile != null }.map { it.tile!! }.toList()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}

data class ComponentSlot(var tile: TileInstance?)

interface ComponentRequirement {
    val slotAmount: Int

    val reqString: String

    val description: String

    fun find(hand: List<TileInstance>): List<List<TileInstance>>

    fun satisfied(slots: List<ComponentSlot>): Boolean
}

private fun generateSlots(amount: Int): List<ComponentSlot> {
    val slots = mutableListOf<ComponentSlot>()
    repeat(amount) {
        slots.add(ComponentSlot(null))
    }
    return slots
}

private fun suitReqString(allowedSuits: Set<Suit>): String {
    return when (allowedSuits) {
        setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR) -> "@Any"
        setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING) -> "@Elemental"
        setOf(Suit.LIFE, Suit.STAR) -> "@Arcane"
        setOf(Suit.FIRE) -> "@Fire"
        setOf(Suit.ICE) -> "@Ice"
        setOf(Suit.LIGHTNING) -> "@Lightning"
        setOf(Suit.LIFE) -> "@Life"
        setOf(Suit.STAR) -> "@Star"
        else -> ""
    }
}

class Single(private val allowedSuits: Set<Suit>) : ComponentRequirement {
    constructor() : this(setOf(Suit.FIRE, Suit.ICE, Suit.LIGHTNING, Suit.LIFE, Suit.STAR))

    override val slotAmount = 1

    override val reqString = "1 ${suitReqString(allowedSuits)}"

    override val description = "A single tile"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        return hand
                .filter { it.tile.suit in allowedSuits }
                .map { listOf(it) }
    }

    override fun satisfied(slots: List<ComponentSlot>): Boolean {
        return slots.size == 1
                && slots.firstOrNull()?.tile?.let { allowedSuits.contains(it.tile.suit) } ?: false
    }
}
