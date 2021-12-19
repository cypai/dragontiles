package com.pipai.dragontiles.sorceries

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.Identical
import com.pipai.dragontiles.spells.Sequential
import com.pipai.dragontiles.utils.with
import com.pipai.dragontiles.utils.withoutAll

abstract class Sorcery {
    abstract val id: String
    abstract suspend fun onCast(hand: FullCastHand, api: CombatApi)
}

fun findFullCastHand(hand: List<TileInstance>, fullHandSize: Int): List<FullCastHand> {
    if (hand.size != fullHandSize) {
        return listOf()
    }
    return dedupe(findFullCastEyeIteration(hand, fullHandSize))
}

private fun dedupe(fcs: List<FullCastHand>): List<FullCastHand> {
    val returnList = fcs.toMutableList()
    val copy = fcs.toList()
    copy.forEach { h ->
        if (h in returnList) {
            returnList.removeAll { it != h && it.uniqueIdentifier() == h.uniqueIdentifier() }
        }
    }
    return returnList
}

private val pair = Identical(2)
private val identical = Identical(3)
private val sequential = Sequential(3)

private fun findFullCastEyeIteration(hand: List<TileInstance>, fullHandSize: Int): List<FullCastHand> {
    val meldAmount = fullHandSize / 3
    val fullCastHands: MutableList<FullCastHand> = mutableListOf()
    val eyes = pair.find(hand)
    eyes.map { eye ->
        val partials = findFullCastMeldIteration(listOf(), hand.withoutAll(eye))
        fullCastHands.addAll(partials.filter { it.size == meldAmount }.map { FullCastHand(it, eye) })
    }
    return fullCastHands
}

private fun findFullCastMeldIteration(current: List<Meld>, hand: List<TileInstance>): List<List<Meld>> {
    if (hand.isEmpty()) {
        return listOf(current)
    }
    val melds = identical.find(hand)
            .map { Meld(it, MeldType.IDENTICAL) }
            .toMutableList()
    sequential.find(hand)
            .map { Meld(it, MeldType.SEQUENCE) }
            .forEach { melds.add(it) }
    if (melds.isEmpty()) {
        return listOf(current)
    }
    val partialHand: MutableList<List<Meld>> = mutableListOf()
    melds.forEach {
        partialHand.addAll(
                findFullCastMeldIteration(current.with(it), hand.withoutAll(it.tiles))
                        .with(current))
    }
    return partialHand
}

data class FullCastHand(val melds: List<Meld>, val eye: List<TileInstance>) {
    fun uniqueIdentifier(): List<Any> {
        return melds.map { m -> Pair(m.tiles.first().tile, m.type) }
                .sortedBy { it.first.order() }
                .with(eye.map { it.tile })
    }
}

data class Meld(val tiles: List<TileInstance>, val type: MeldType)

enum class MeldType {
    IDENTICAL, SEQUENCE
}
