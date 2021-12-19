package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.utils.*
import org.apache.commons.lang3.builder.ToStringBuilder
import kotlin.reflect.full.createInstance

abstract class Spell : DamageAdjustable {
    abstract val id: String

    abstract val requirement: ComponentRequirement
    abstract val type: SpellType
    abstract val rarity: Rarity
    abstract val aspects: MutableList<SpellAspect>
    private val upgrades: MutableList<SpellUpgrade> = mutableListOf()

    protected val data: MutableMap<String, Int> = mutableMapOf()

    fun newClone(): Spell {
        val clone = this::class.createInstance()
        clone.upgrades.addAll(upgrades)
        return clone
    }

    fun getUpgrades() = upgrades.toList()

    fun upgrade(upgrade: SpellUpgrade) {
        upgrade.onUpgrade(this)
        upgrades.add(upgrade)
    }

    abstract fun available(): Boolean

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int = 0

    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float = 1f

    open fun dynamicValue(key: String, api: CombatApi, params: CastParams): Int {
        return when (key) {
            "!d" -> {
                return if (params.targets.isEmpty()) {
                    // TODO: Fix this
                    api.calculateBaseDamage(Element.NONE, baseDamage())
                } else {
                    val target = api.getEnemy(params.targets.first())
                    api.calculateDamageOnEnemy(target, elemental(components()), baseDamage())
                }
            }
            "!f" -> baseFluxLoss()
            else -> data[key] ?: 0
        }
    }

    abstract fun turnReset()

    abstract fun combatReset()

    fun components() = requirement.componentSlots.filter { it.tile != null }.map { it.tile!! }.toList()

    fun fill(components: List<TileInstance>) {
        requirement.componentSlots.clear()
        components.forEach {
            requirement.componentSlots.add(ComponentSlot(it))
        }
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}

enum class Rarity {
    COMMON, UNCOMMON, RARE
}

abstract class StandardSpell : Spell() {
    private val logger = getLogger()

    abstract val targetType: TargetType

    var repeated = 0
    var exhausted = false

    override fun dynamicValue(key: String, api: CombatApi, params: CastParams): Int {
        return when (key) {
            "!r" -> (aspects.findAs(LimitedRepeatableAspect::class)?.max ?: 1) - repeated
            else -> super.dynamicValue(key, api, params)
        }
    }

    override fun available(): Boolean {
        val repeatOk = aspects.findAs(RepeatableAspect::class) != null
                || repeated < aspects.findAs(LimitedRepeatableAspect::class)?.max ?: 1
        return !exhausted && repeatOk
    }

    suspend fun cast(params: CastParams, api: CombatApi) {
        if (available() && !requirement.satisfied(requirement.componentSlots.mapNotNull { it.tile })) {
            logger.error("Attempted to cast without being ready. State: $this")
            return
        }
        api.castSpell(this)
        handleComponents(api)
        onCast(params, api)
        repeated++
    }

    protected abstract suspend fun onCast(params: CastParams, api: CombatApi)

    open suspend fun handleComponents(api: CombatApi) {
        api.consume(components())
    }

    override fun turnReset() {
        repeated = 0
    }

    override fun combatReset() {
        repeated = 0
        exhausted = false
        data.clear()
    }
}

abstract class Rune : Spell() {
    private val logger = getLogger()

    override val type: SpellType = SpellType.RUNE

    var active = false
    var canActivate = true

    override fun available(): Boolean = active || (!active && canActivate)

    suspend fun activate(api: CombatApi) {
        if (active || !canActivate || !requirement.satisfied(components())) {
            logger.error("Attempted activation when canActivate is false. State: $this")
            return
        }
        active = true
        canActivate = false
        api.activateRune(this, components())
    }

    protected open suspend fun onActivate(api: CombatApi) {
    }

    suspend fun deactivate(api: CombatApi) {
        if (!active) {
            logger.error("Attempted deactivation when canDeactivate is false. State: $this")
            return
        }
        active = false
        api.deactivateRune(this)
    }

    protected open suspend fun onDeactivate(api: CombatApi) {
    }

    override fun combatReset() {
        active = false
        canActivate = true
    }

    override fun turnReset() {
        canActivate = true
        data.clear()
    }
}

abstract class PowerSpell : Spell() {
    private val logger = getLogger()

    override val type: SpellType = SpellType.POWER

    var powered = false

    override fun available(): Boolean = !powered

    suspend fun cast(params: CastParams, api: CombatApi) {
        if (available() && !requirement.satisfied(requirement.componentSlots.mapNotNull { it.tile })) {
            logger.error("Attempted to cast without being ready. State: $this")
            return
        }
        api.castSpell(this)
        handleComponents(api)
        onCast(params, api)
        powered = true
    }

    protected abstract suspend fun onCast(params: CastParams, api: CombatApi)

    open suspend fun handleComponents(api: CombatApi) {
        api.consume(components())
    }

    override fun combatReset() {
        powered = false
    }

    override fun turnReset() {
        powered = false
        data.clear()
    }
}

enum class SpellType {
    ATTACK, EFFECT, POWER, RUNE
}

enum class TargetType {
    SINGLE, SINGLE_ENEMY, SINGLE_CA, AOE, NONE
}

data class CastParams(val targets: List<Int>)

data class ComponentSlot(var tile: TileInstance?)

abstract class ComponentRequirement {
    abstract val description: String
    abstract val type: SetType
    abstract var suitGroup: SuitGroup
    abstract val reqAmount: ReqAmount
    open val manualOnly: Boolean = false
    val componentSlots: MutableList<ComponentSlot> = mutableListOf()

    abstract fun find(hand: List<TileInstance>): List<List<TileInstance>>
    abstract fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>>
    abstract fun satisfied(slots: List<TileInstance>): Boolean
}

abstract class ManualComponentRequirement : ComponentRequirement() {
    override val manualOnly = true

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        return listOf()
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        return if (satisfied(given)) {
            listOf(given)
        } else {
            listOf()
        }
    }
}

enum class SetType {
    MISC, IDENTICAL, SEQUENTIAL
}

enum class SuitGroup(val allowedSuits: Set<Suit>, val isElemental: Boolean) {
    FIRE(setOf(Suit.FIRE), true),
    ICE(setOf(Suit.ICE), true),
    LIGHTNING(setOf(Suit.LIGHTNING), true),
    STAR(setOf(Suit.STAR), false),
    LIFE(setOf(Suit.LIFE), false),
    ELEMENTAL(elementalSet, true),
    ARCANE(arcaneSet, false),
    ANY(anySet, false),
}

sealed class ReqAmount {
    abstract fun text(): String

    data class ImmutableNumeric(val amount: Int) : ReqAmount() {
        override fun text(): String = amount.toString()
    }

    data class Numeric(var amount: Int) : ReqAmount() {
        override fun text(): String = amount.toString()
    }

    class XAmount : ReqAmount() {
        override fun text(): String = "x"
    }
}

open class Single(override var suitGroup: SuitGroup) : ComponentRequirement() {
    constructor() : this(SuitGroup.ANY)

    override var type = SetType.MISC
    override val reqAmount = ReqAmount.ImmutableNumeric(1)
    override val description = "A single tile"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        return hand
            .filter { it.tile.suit in suitGroup.allowedSuits }
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
                && slots.firstOrNull()?.tile?.let { suitGroup.allowedSuits.contains(it.suit) } ?: false
    }
}

class SinglePredicate(
    private val predicate: (TileInstance) -> Boolean,
    suitGroup: SuitGroup
) : Single(suitGroup) {

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

class AnyCombo(slotAmount: Int, override var suitGroup: SuitGroup) : ManualComponentRequirement() {
    constructor(slotAmount: Int) : this(slotAmount, SuitGroup.ANY)

    override val type = SetType.MISC
    override val reqAmount = ReqAmount.Numeric(slotAmount)
    override val description = "Any $slotAmount tiles"

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return slots.size == reqAmount.amount
                && slots.all { it.tile.suit in suitGroup.allowedSuits }
    }
}

class Identical(slotAmount: Int, override var suitGroup: SuitGroup) : ComponentRequirement() {
    constructor(slotAmount: Int) : this(slotAmount, SuitGroup.ANY)

    override val type = SetType.IDENTICAL
    override val reqAmount = ReqAmount.Numeric(slotAmount)
    override val description = "A set of $slotAmount identical tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val count: MutableMap<Tile, MutableList<TileInstance>> = mutableMapOf()
        hand.filter { it.tile.suit in suitGroup.allowedSuits }
            .forEach {
                if (it.tile in count) {
                    val list = count[it.tile]!!
                    if (list.size < reqAmount.amount) {
                        list.add(it)
                    }
                } else {
                    count[it.tile] = mutableListOf(it)
                }
            }
        return count.filterValues { it.size >= reqAmount.amount }
            .values
            .toList()
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        if (given.size > reqAmount.amount) {
            return listOf()
        }
        return when (given.size) {
            0 -> find(hand)
            1 -> find(hand.filter { it.tile == given.first().tile })
            reqAmount.amount -> {
                if (given.all { it.tile == given.first().tile }) {
                    listOf(given)
                } else {
                    listOf()
                }
            }
            else -> {
                val first = given.first()
                if (given.all { it.tile == first.tile }) {
                    Identical(reqAmount.amount - given.size, suitGroup)
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
        return slots.size == reqAmount.amount
                && first?.suit in suitGroup.allowedSuits
                && slots.all { it.tile == first }
    }
}

class IdenticalX(override var suitGroup: SuitGroup) : ComponentRequirement() {
    constructor() : this(SuitGroup.ANY)

    override val type = SetType.IDENTICAL
    override val reqAmount = ReqAmount.XAmount()
    override val description = "A variable set of identical tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val sets: MutableList<List<TileInstance>> = mutableListOf()
        sets.addAll(hand.filter { it.tile.suit in suitGroup.allowedSuits }.map { listOf(it) })
        var x = 2
        while (true) {
            val foundSets = Identical(x, suitGroup).find(hand)
            if (foundSets.isEmpty()) {
                break
            } else {
                sets.addAll(foundSets)
            }
            x++
        }
        return sets
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        when (given.size) {
            0 -> return find(hand)
            1 -> return find(hand.filter { it.tile == given.first().tile }).map { it.with(given.first()) }
            else -> {
                val sets: MutableList<List<TileInstance>> = mutableListOf(given)
                var x = given.size + 1
                val tile = given.first().tile
                while (true) {
                    val foundSets = Identical(x, suitGroup).find(hand.filter { it.tile == tile })
                    if (foundSets.isEmpty()) {
                        break
                    } else {
                        sets.addAll(foundSets)
                    }
                    x++
                }
                return sets
            }
        }
    }

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return slots.isNotEmpty() && slots.all { it.tile == slots.first().tile }
    }
}

class Sequential(slotAmount: Int, override var suitGroup: SuitGroup) : ComponentRequirement() {
    constructor(slotAmount: Int) : this(slotAmount, SuitGroup.ELEMENTAL)

    override val type = SetType.SEQUENTIAL
    override val reqAmount = ReqAmount.Numeric(slotAmount)
    override val description = "A set of $slotAmount sequential tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val sequences: MutableMap<TileInstance, MutableList<TileInstance>> = hand
            .filter { it.tile.suit in suitGroup.allowedSuits }
            .associateWith { mutableListOf(it) }
            .toMutableMap()

        hand.filter { it.tile.suit in suitGroup.allowedSuits }
            .forEach {
                val tile = it.tile as Tile.ElementalTile
                sequences.values.forEach { s ->
                    val last = s.last().tile as Tile.ElementalTile
                    if (s.size < reqAmount.amount && last.suit == tile.suit && last.number == tile.number - 1) {
                        s.add(it)
                    }
                }
            }
        return sequences.filterValues { it.size >= reqAmount.amount }
            .values
            .toList()
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        if (given.size > reqAmount.amount) {
            return listOf()
        }
        return when (given.size) {
            0 -> find(hand)
            else -> {
                val first = given.first().tile
                if (first !is Tile.ElementalTile
                    || !given.all { it.tile.suit in suitGroup.allowedSuits && it.tile.suit == first.suit }
                    || !sequential(given.map { it.tile as Tile.ElementalTile })
                ) {
                    return listOf()
                }
                if (given.size == reqAmount.amount) {
                    return listOf(given)
                }
                return find(hand.filter { handTile ->
                    val tile = handTile.tile
                    val givenTiles = given.map { it.tile as Tile.ElementalTile }
                    val minimum = givenTiles.minByOrNull { it.number }!!.number - reqAmount.amount
                    val maximum = givenTiles.maxByOrNull { it.number }!!.number + reqAmount.amount
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
        return slots.size == reqAmount.amount
                && slots.all { it.tile.suit in suitGroup.allowedSuits }
                && sequential(slots.map { it.tile as Tile.ElementalTile })
    }

    private fun sequential(tiles: List<Tile.ElementalTile>): Boolean {
        return tiles.windowed(2)
            .all { it[0].number == it[1].number - 1 }
    }
}

class SequentialX(override var suitGroup: SuitGroup) : ComponentRequirement() {
    constructor() : this(SuitGroup.ELEMENTAL)

    override val type = SetType.SEQUENTIAL
    override val reqAmount = ReqAmount.XAmount()
    override val description = "A variable set of sequential tiles"

    override fun find(hand: List<TileInstance>): List<List<TileInstance>> {
        val sets: MutableList<List<TileInstance>> = mutableListOf()
        sets.addAll(hand.filter { it.tile.suit in suitGroup.allowedSuits }.map { listOf(it) })
        var x = 2
        while (true) {
            val foundSets = Sequential(x, suitGroup).find(hand)
            if (foundSets.isEmpty()) {
                break
            } else {
                sets.addAll(foundSets)
            }
            x++
        }
        return sets
    }

    override fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>> {
        when (given.size) {
            0 -> return find(hand)
            1 -> {
                val first = given.first()
                return find(hand.filter { it.tile.suit == first.tile.suit })
                    .filter { it.contains(first) }
                    .map { it.with(given.first()) }
            }
            else -> {
                val sets: MutableList<List<TileInstance>> = mutableListOf(given)
                var x = given.size + 1
                val tile = given.first().tile
                while (true) {
                    val foundSets = Sequential(x, suitGroup).find(hand.filter { it.tile == tile })
                    if (foundSets.isEmpty()) {
                        break
                    } else {
                        sets.addAll(foundSets)
                    }
                    x++
                }
                return sets
            }
        }
    }

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return slots.isNotEmpty()
                && slots.all { it.tile.suit in suitGroup.allowedSuits }
                && sequential(slots.map { it.tile as Tile.ElementalTile })
    }

    private fun sequential(tiles: List<Tile.ElementalTile>): Boolean {
        return tiles.windowed(2)
            .all { it[0].number == it[1].number - 1 }
    }
}
