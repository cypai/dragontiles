package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.utils.with
import com.pipai.dragontiles.utils.withoutAll
import kotlinx.serialization.Serializable

@Serializable
abstract class Sorcery : Spell() {
    final override val type = SpellType.SORCERY

    override fun flags(): List<CombatFlag> {
        return super.flags().with(CombatFlag.SORCERY)
    }

    override fun swappableFromSideboard(): Boolean = false

    override fun available(): Boolean {
        throw NotImplementedError()
    }

    suspend fun cast(hand: FullCastHand, api: CombatApi) {
        if (scoreable) {
            api.score()
        }
        onCast(hand, api)
        aspects.forEach { it.onCast(this, api) }
    }

    abstract suspend fun onCast(hand: FullCastHand, api: CombatApi)

    override fun turnReset() {
    }

    override fun combatReset() {
        data.clear()
    }
}

fun findFullCastHand(hand: List<TileInstance>): List<FullCastHand> {
    return dedupe(findFullCastEyeIteration(hand))
}

private fun dedupe(fcs: List<FullCastHand>): List<FullCastHand> {
    val returnList: MutableList<FullCastHand> = mutableListOf()
    val ids: MutableSet<String> = mutableSetOf()
    fcs.forEach {
        val id = it.uniqueIdentifier().toString()
        if (id !in ids) {
            ids.add(id)
            returnList.add(it)
        }
    }
    return returnList
}

private val pair = Identical(2)
private val identical = Identical(3)
private val sequential = Sequential(3)

private fun findFullCastEyeIteration(hand: List<TileInstance>): List<FullCastHand> {
    if (hand.size % 3 != 0 && (hand.size - 2) % 3 != 0) {
        return listOf()
    }
    val fullCastHands: MutableList<FullCastHand> = mutableListOf()
    val eyes = pair.find(hand)
    if (eyes.isNotEmpty() && hand.size == 2) {
        fullCastHands.add(FullCastHand(listOf(), eyes.first()))
        return fullCastHands
    }
    eyes.map { eye ->
        val acceptableHands = findFullCastMeldIteration(hand.withoutAll(eye))
        fullCastHands.addAll(acceptableHands
            .map { f ->
                FullCastHand(
                    f.sortedWith(
                        compareBy(
                            { it.tiles.first().tile.suit.order },
                            { it.tiles.first().tile.order() },
                            { it.type })
                    ), eye
                )
            }
        )
    }
    fullCastHands.addAll(
        findFullCastMeldIteration(hand)
            .map { f ->
                FullCastHand(
                    f.sortedWith(
                        compareBy(
                            { it.tiles.first().tile.suit.order },
                            { it.tiles.first().tile.order() },
                            { it.type })
                    ), listOf()
                )
            })
    return fullCastHands
}

private fun findFullCastMeldIteration(hand: List<TileInstance>): List<List<Meld>> {
    val melds = identical.find(hand)
        .map { Meld(it, MeldType.IDENTICAL) }
        .toMutableList()
    sequential.find(hand)
        .map { Meld(it, MeldType.SEQUENCE) }
        .forEach { melds.add(it) }
    if (melds.isEmpty()) {
        return listOf()
    }
    if (hand.size == 3) {
        return melds.map { meld -> listOf(meld) }
    }
    val acceptableHands: MutableList<List<Meld>> = mutableListOf()
    // Pick a meld. Look for hands without that meld. Return a list of those hands with your meld appended.
    melds.forEach { meld ->
        findFullCastMeldIteration(hand.withoutAll(meld.tiles))
            .map { h -> h.with(meld) }
            .forEach { acceptableHands.add(it) }
    }
    return acceptableHands
}

data class FullCastHand(val melds: List<Meld>, val eye: List<TileInstance>) {
    fun uniqueIdentifier(): List<Any> {
        return melds
            .map { m -> Pair(m.tiles.first().tile, m.type) }
            .sortedWith(compareBy({ it.first.suit.order }, { it.first.order() }, { it.second }))
            .with(eye.map { it.tile })
    }
}

data class Meld(val tiles: List<TileInstance>, val type: MeldType)

enum class MeldType {
    IDENTICAL, SEQUENCE
}
