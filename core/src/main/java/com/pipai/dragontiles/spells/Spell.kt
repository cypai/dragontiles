package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.getLogger
import org.apache.commons.lang3.builder.ToStringBuilder

abstract class Spell(var upgraded: Boolean) {
    private val logger = getLogger()

    abstract val id: String
    abstract val requirement: ComponentRequirement
    abstract val targetType: TargetType

    abstract var repeatableMax: Int

    var repeated = 0
    var exhausted = false

    private val data: MutableMap<String, Int> = mutableMapOf()

    protected abstract fun newClone(upgraded: Boolean): Spell

    open fun baseDamage(): Int = 0

    fun dynamicValue(key: String, api: CombatApi, params: CastParams): Int {
        return when (key) {
            "!r" -> repeatableMax - repeated
            "!d" -> {
                return if (params.targets.isEmpty()) {
                    api.calculateBaseDamage(api.combat.heroStatus, baseDamage())
                } else {
                    api.calculateTargetDamage(params.targets.first(), elemental(components()), baseDamage())
                }
            }
            else -> data[key] ?: 0
        }
    }

    fun available() = !exhausted && repeated < repeatableMax

    fun ready() = available() && requirement.satisfied(requirement.componentSlots)

    suspend fun cast(params: CastParams, api: CombatApi) {
        if (!ready()) {
            logger.error("Attempted to cast without being ready. State: $this")
            return
        }
        api.castSpell(this)
        onCast(params, api)
        handleComponents(api)
        repeated++
    }

    protected abstract suspend fun onCast(params: CastParams, api: CombatApi)

    open suspend fun handleComponents(api: CombatApi) {
        api.consume(components())
    }

    fun turnReset() {
        repeated = 0
    }

    fun combatReset() {
        exhausted = false
        repeated = 0
        data.clear()
    }

    fun fill(components: List<TileInstance>) {
        requirement.componentSlots.zip(components) { slot, tile ->
            slot.tile = tile
        }
    }

    fun components() = requirement.componentSlots.filter { it.tile != null }.map { it.tile!! }.toList()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}

enum class TargetType {
    SINGLE, AOE, NONE
}

data class CastParams(val targets: List<Enemy>)

data class ComponentSlot(var tile: TileInstance?)

abstract class ComponentRequirement(val slotAmount: Int) {
    abstract val reqString: String
    abstract val description: String
    val componentSlots: List<ComponentSlot> = generateSlots(slotAmount)

    abstract fun find(hand: List<TileInstance>): List<List<TileInstance>>
    abstract fun satisfied(slots: List<ComponentSlot>): Boolean
}

fun generateSlots(amount: Int): List<ComponentSlot> {
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

class Single(private val allowedSuits: Set<Suit>) : ComponentRequirement(1) {
    constructor() : this(anySet)

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

class Identical(slotAmount: Int, private val allowedSuits: Set<Suit>) : ComponentRequirement(slotAmount) {
    constructor(slotAmount: Int) : this(slotAmount, anySet)

    override val reqString = "$slotAmount I ${suitReqString(allowedSuits)}"

    override val description = "A set of $slotAmount identical tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val count: MutableMap<Tile, MutableList<TileInstance>> = mutableMapOf()
        hand.filter { it.tile.suit in allowedSuits }
                .forEach {
                    if (it.tile in count) {
                        val list = count[it.tile]!!
                        if (list.size < slotAmount) {
                            list.add(it)
                        }
                    } else {
                        count[it.tile] = mutableListOf(it)
                    }
                }
        return count.filterValues { it.size >= slotAmount }
                .values
                .toList()
    }

    override fun satisfied(slots: List<ComponentSlot>): Boolean {
        val first = slots.firstOrNull()?.tile
        return slots.size == slotAmount
                && first?.tile?.suit in allowedSuits
                && slots.all { it.tile != null && it.tile?.tile == first?.tile }
    }
}

class Sequential(slotAmount: Int, private val allowedSuits: Set<Suit>) : ComponentRequirement(slotAmount) {
    constructor(slotAmount: Int) : this(slotAmount, elementalSet)

    override val reqString = "$slotAmount S ${suitReqString(allowedSuits)}"

    override val description = "A set of $slotAmount sequential tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val sequences: MutableMap<Tile, MutableList<TileInstance>> = mutableMapOf()
        hand.filter { it.tile.suit in allowedSuits }
                .forEach {
                    val tile = it.tile as Tile.ElementalTile
                    if (tile !in sequences) {
                        sequences[tile] = mutableListOf(it)
                        sequences.values.forEach { s ->
                            val last = s.last().tile as Tile.ElementalTile
                            if (s.size < slotAmount && last.suit == tile.suit && last.number == tile.number - 1) {
                                s.add(it)
                            }
                        }
                    }
                }
        return sequences.filterValues { it.size >= slotAmount }
                .values
                .toList()
    }

    override fun satisfied(slots: List<ComponentSlot>): Boolean {
        return slots.size == slotAmount
                && slots.all { it.tile != null && it.tile?.tile?.suit in allowedSuits }
                && slots.windowed(2)
                .all { (it[0].tile?.tile as Tile.ElementalTile).number == (it[1].tile?.tile as Tile.ElementalTile).number - 1 }
    }
}
