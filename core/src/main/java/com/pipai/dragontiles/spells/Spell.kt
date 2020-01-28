package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.utils.getLogger
import com.pipai.dragontiles.utils.withAll
import com.pipai.dragontiles.utils.withoutAll
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
                    val target = api.getTargetable(params.targets.first())
                    api.calculateTargetDamage(target, elemental(components()), baseDamage())
                }
            }
            else -> data[key] ?: 0
        }
    }

    fun available() = !exhausted && repeated < repeatableMax

    fun ready() = available() && requirement.satisfied(requirement.componentSlots.mapNotNull { it.tile })

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
        requirement.componentSlots.clear()
        components.forEach {
            requirement.componentSlots.add(ComponentSlot(it))
        }
    }

    fun components() = requirement.componentSlots.filter { it.tile != null }.map { it.tile!! }.toList()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}

enum class TargetType {
    SINGLE, SINGLE_ENEMY, SINGLE_CA, AOE, NONE
}

data class CastParams(val targets: List<Int>)

data class ComponentSlot(var tile: TileInstance?)

abstract class ComponentRequirement {
    abstract val reqString: String
    abstract val description: String
    val componentSlots: MutableList<ComponentSlot> = mutableListOf()

    abstract fun find(hand: List<TileInstance>): List<List<TileInstance>>
    abstract fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>>
    abstract fun satisfied(slots: List<TileInstance>): Boolean
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

open class Single(private val allowedSuits: Set<Suit>) : ComponentRequirement() {
    constructor() : this(anySet)

    override val reqString = "1 ${suitReqString(allowedSuits)}"

    override val description = "A single tile"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        return hand
                .filter { it.tile.suit in allowedSuits }
                .map { listOf(it) }
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        return when (given.size) {
            0 -> find(hand)
            1 -> hand.withoutAll(given).map { listOf(it) }
            else -> listOf()
        }
    }

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return slots.size == 1
                && slots.firstOrNull()?.tile?.let { allowedSuits.contains(it.suit) } ?: false
    }
}

class SinglePredicate(private val predicate: (TileInstance) -> Boolean,
                      allowedSuits: Set<Suit>) : Single(allowedSuits) {

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        return super.find(hand)
                .filter { predicate.invoke(it[0]) }
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        return super.findGiven(hand, given)
                .filter { predicate.invoke(it[0]) }
    }

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return super.satisfied(slots) && predicate.invoke(slots[0])
    }

}

class Identical(var slotAmount: Int, private val allowedSuits: Set<Suit>) : ComponentRequirement() {
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

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        if (given.size > slotAmount) {
            return listOf()
        }
        return when (given.size) {
            0 -> find(hand)
            1 -> find(hand.filter { it.tile == given.first().tile })
            slotAmount -> {
                if (given.all { it.tile == given.first().tile }) {
                    listOf(given)
                } else {
                    listOf()
                }
            }
            else -> {
                val first = given.first()
                if (given.all { it.tile == first.tile }) {
                    Identical(slotAmount - given.size, allowedSuits)
                            .find(hand.withoutAll(given).filter { it.tile == first.tile })
                            .map { it.withAll(given) }
                } else {
                    listOf()
                }
            }
        }
    }

    override fun satisfied(slots: List<TileInstance>): Boolean {
        val first = slots.firstOrNull()?.tile
        return slots.size == slotAmount
                && first?.suit in allowedSuits
                && slots.all { it.tile == first }
    }
}

class Sequential(var slotAmount: Int, private val allowedSuits: Set<Suit>) : ComponentRequirement() {
    constructor(slotAmount: Int) : this(slotAmount, elementalSet)

    override val reqString = "$slotAmount S ${suitReqString(allowedSuits)}"

    override val description = "A set of $slotAmount sequential tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val sequences: MutableMap<TileInstance, MutableList<TileInstance>> = hand
                .filter { it.tile.suit in allowedSuits }
                .associateWith { mutableListOf(it) }
                .toMutableMap()

        hand.filter { it.tile.suit in allowedSuits }
                .forEach {
                    val tile = it.tile as Tile.ElementalTile
                    sequences.values.forEach { s ->
                        val last = s.last().tile as Tile.ElementalTile
                        if (s.size < slotAmount && last.suit == tile.suit && last.number == tile.number - 1) {
                            s.add(it)
                        }
                    }
                }
        return sequences.filterValues { it.size >= slotAmount }
                .values
                .toList()
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        if (given.size > slotAmount) {
            return listOf()
        }
        return when (given.size) {
            0 -> find(hand)
            else -> {
                val first = given.first().tile
                if (first !is Tile.ElementalTile
                        || !given.all { it.tile.suit in allowedSuits && it.tile.suit == first.suit }
                        || !sequential(given.map { it.tile as Tile.ElementalTile })) {
                    return listOf()
                }
                if (given.size == slotAmount) {
                    return listOf(given)
                }
                return find(hand.filter { handTile ->
                    val tile = handTile.tile
                    val givenTiles = given.map { it.tile as Tile.ElementalTile }
                    val minimum = givenTiles.minBy { it.number }!!.number - slotAmount
                    val maximum = givenTiles.maxBy { it.number }!!.number + slotAmount
                    tile is Tile.ElementalTile
                            && tile.suit == first.suit
                            && tile.number >= minimum
                            && tile.number <= maximum
                            && (handTile in given || tile.number !in givenTiles.map { it.number })
                })
            }
        }
    }

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return slots.size == slotAmount
                && slots.all { it.tile.suit in allowedSuits }
                && sequential(slots.map { it.tile as Tile.ElementalTile })
    }

    private fun sequential(tiles: List<Tile.ElementalTile>): Boolean {
        return tiles.windowed(2)
                .all { it[0].number == it[1].number - 1 }
    }
}
