package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import com.pipai.dragontiles.utils.*
import kotlinx.serialization.Serializable
import org.apache.commons.lang3.builder.ToStringBuilder
import kotlin.reflect.full.createInstance

data class SpellInstance(
    override val id: String,
    val upgrades: MutableList<SpellUpgradeInstance>,
) : Localized {

    fun toSpell(gameData: GameData): Spell {
        return gameData.getSpell(id).withUpgrades(upgrades.map { gameData.getSpellUpgrade(it.id) })
    }
}

data class SpellUpgradeInstance(
    override val id: String,
) : Localized

abstract class Spell : Localized, DamageAdjustable {
    abstract val requirement: ComponentRequirement
    abstract val type: SpellType
    abstract val rarity: Rarity
    abstract val aspects: MutableList<SpellAspect>
    open fun flags(): List<CombatFlag> = listOf()
    private val upgrades: MutableList<SpellUpgrade> = mutableListOf()
    open val additionalKeywords: List<String> = listOf()

    protected val data: MutableMap<String, Int> = mutableMapOf()

    fun newClone(): Spell {
        val clone = this::class.createInstance()
        clone.upgrades.addAll(upgrades)
        return clone
    }

    fun toInstance(): SpellInstance {
        return SpellInstance(id, upgrades.map { SpellUpgradeInstance(it.id) }.toMutableList())
    }

    fun getUpgrades() = upgrades.toList()

    fun upgrade(upgrade: SpellUpgrade) {
        upgrade.onUpgrade(this)
        upgrades.add(upgrade)
    }

    fun withUpgrades(upgrades: List<SpellUpgrade>): Spell {
        upgrades.forEach {
            upgrade(it)
        }
        return this
    }

    abstract fun swappableFromSideboard(): Boolean

    abstract fun available(): Boolean

    override fun queryFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int = 0

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float = 1f

    open fun dynamicBaseDamage(components: List<TileInstance>): Int {
        return baseDamage()
    }

    open fun dynamicValue(key: String, api: CombatApi?, params: CastParams): Int {
        return when (key) {
            "!d" -> {
                return if (api == null) {
                    baseDamage()
                } else {
                    if (params.targets.isEmpty()) {
                        // TODO: Fix this
                        api.calculateBaseDamage(Element.NONE, baseDamage())
                    } else {
                        val target = api.getEnemy(params.targets.first())
                        api.calculateDamageOnEnemy(target, elemental(components()), dynamicBaseDamage(components()), flags())
                    }
                }
            }
            "!dp" -> {
                // Dynamic Plus: For cases where description looks like !d + PowerUpgrade
                return if (api == null) {
                    baseDamage()
                } else {
                    if (params.targets.isEmpty()) {
                        // TODO: Fix this
                        api.calculateBaseDamage(Element.NONE, baseDamage())
                    } else {
                        val target = api.getEnemy(params.targets.first())
                        api.calculateDamageOnEnemy(target, elemental(components()), baseDamage(), flags())
                    }
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
    STARTER, COMMON, UNCOMMON, RARE, SPECIAL
}

@Serializable
abstract class StandardSpell : Spell() {
    private val logger = getLogger()

    abstract val targetType: TargetType

    var repeated = 0
    var exhausted = false

    override fun swappableFromSideboard(): Boolean = !exhausted

    override fun dynamicValue(key: String, api: CombatApi?, params: CastParams): Int {
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
        handleComponents(api)
        onCast(params, api)
        api.castSpell(this) // After casting to have effects on spellcast resolve in the proper order
        repeated++
    }

    protected abstract suspend fun onCast(params: CastParams, api: CombatApi)

    open suspend fun handleComponents(api: CombatApi) {
        api.consume(components(), this)
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

    override fun swappableFromSideboard(): Boolean = true

    override fun available(): Boolean = active || (!active && canActivate)

    suspend fun activate(api: CombatApi) {
        if (active || !canActivate || !requirement.satisfied(components())) {
            logger.error("Attempted activation when canActivate is false. State: $this")
            return
        }
        active = true
        canActivate = false
        api.activateRune(this, components())
        onActivate(api)
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
        onDeactivate(api)
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

    override fun swappableFromSideboard(): Boolean = !powered

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
        api.consume(components(), this)
    }

    override fun combatReset() {
        powered = false
    }

    override fun turnReset() {
        data.clear()
    }
}

enum class SpellType {
    ATTACK, EFFECT, POWER, RUNE, SORCERY
}

enum class TargetType {
    SINGLE, SINGLE_ENEMY, SINGLE_CA, AOE, NONE
}

data class CastParams(val targets: List<Int>)

data class ComponentSlot(var tile: TileInstance?)

interface ComponentRequirement {
    val description: String
    val type: SetType
    var suitGroup: SuitGroup
    val reqAmount: ReqAmount
    val manualOnly: Boolean
    val componentSlots: MutableList<ComponentSlot>

    fun find(hand: List<TileInstance>): List<List<TileInstance>>
    fun findGiven(hand: List<TileInstance>, given: List<TileInstance>): List<List<TileInstance>>
    fun satisfied(slots: List<TileInstance>): Boolean
    fun satisfied(fullCastHand: FullCastHand): Boolean {
        return fullCastHand.melds.any { satisfied(it.tiles) } || satisfied(fullCastHand.eye)
    }
}

abstract class ManualComponentRequirement : ComponentRequirement {
    override val manualOnly = true
    override val componentSlots: MutableList<ComponentSlot> = mutableListOf()

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
    ANY_NO_FUMBLE(anyNoFumbleSet, false),
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

    class UnknownAmount : ReqAmount() {
        override fun text(): String = "?"
    }
}

open class Single(override var suitGroup: SuitGroup) : ComponentRequirement {
    constructor() : this(SuitGroup.ANY)

    override var type = SetType.MISC
    override val reqAmount = ReqAmount.ImmutableNumeric(1)
    override val description = "A single tile"
    override val componentSlots: MutableList<ComponentSlot> = mutableListOf()
    override val manualOnly = false

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

abstract class CustomRequirement : ManualComponentRequirement() {
    override val reqAmount = ReqAmount.UnknownAmount()
}

class Identical(slotAmount: Int, override var suitGroup: SuitGroup) : ComponentRequirement {
    constructor(slotAmount: Int) : this(slotAmount, SuitGroup.ANY_NO_FUMBLE)

    override val type = SetType.IDENTICAL
    override val reqAmount = ReqAmount.Numeric(slotAmount)
    override val description = "A set of $slotAmount identical tiles"
    override val componentSlots: MutableList<ComponentSlot> = mutableListOf()
    override val manualOnly = false

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

class IdenticalX(override var suitGroup: SuitGroup) : ComponentRequirement {
    constructor() : this(SuitGroup.ANY_NO_FUMBLE)

    override val type = SetType.IDENTICAL
    override val reqAmount = ReqAmount.XAmount()
    override val description = "A variable set of identical tiles"
    override val componentSlots: MutableList<ComponentSlot> = mutableListOf()
    override val manualOnly = false

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

class Sequential(slotAmount: Int, override var suitGroup: SuitGroup) : ComponentRequirement {
    constructor(slotAmount: Int) : this(slotAmount, SuitGroup.ELEMENTAL)

    override val type = SetType.SEQUENTIAL
    override val reqAmount = ReqAmount.Numeric(slotAmount)
    override val description = "A set of $slotAmount sequential tiles"
    override val componentSlots: MutableList<ComponentSlot> = mutableListOf()
    override val manualOnly = false

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

class SequentialX(override var suitGroup: SuitGroup) : ComponentRequirement {
    constructor() : this(SuitGroup.ELEMENTAL)

    override val type = SetType.SEQUENTIAL
    override val reqAmount = ReqAmount.XAmount()
    override val description = "A variable set of sequential tiles"
    override val componentSlots: MutableList<ComponentSlot> = mutableListOf()
    override val manualOnly = false

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

class ForbidTransformFreeze(private val spell: Spell, private val compReq: ComponentRequirement) :
    ComponentRequirement by compReq {

    override fun satisfied(slots: List<TileInstance>): Boolean {
        return compReq.satisfied(slots)
                && (spell.aspects.none { it is TransformAspect } || slots.none { it.tileStatus == TileStatus.FREEZE })
    }
}
