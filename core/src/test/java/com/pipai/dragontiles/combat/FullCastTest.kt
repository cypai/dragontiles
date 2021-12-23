package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.findFullCastHand
import org.junit.Assert
import org.junit.Test

class FullCastTest {

    private var nextId = 0

    private fun nextId(): Int {
        val id = nextId
        nextId++
        return id
    }

    @Test
    fun testLargeHand() {
        val hand: MutableList<TileInstance> = mutableListOf()
        (1..3).forEach { num ->
            repeat(2) {
                hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, num), TileStatus.NONE, nextId()))
            }
        }
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 4), TileStatus.NONE, nextId()))
        }
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, 9), TileStatus.NONE, nextId()))
        }
        repeat(2) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testEyeOnly() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(2) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testSingleIdenticalMeld() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(3) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testSingleSequentialMeld() {
        val hand: MutableList<TileInstance> = mutableListOf()
        (1..3).forEach { num ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, num), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testPartial1() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(2) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, nextId()))
        }
        (1..3).forEach { num ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, num), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testPartial2() {
        val hand: MutableList<TileInstance> = mutableListOf()
        (1..6).forEach { num ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, num), TileStatus.NONE, nextId()))
        }
        (1..3).forEach { num ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, num), TileStatus.NONE, nextId()))
        }
        repeat(2) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testPartial3() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, 3), TileStatus.NONE, nextId()))
        }
        (2..4).forEach { num ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, num), TileStatus.NONE, nextId()))
        }
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, 5), TileStatus.NONE, nextId()))
        }
        repeat(2) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testMixedSingleSuit() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, nextId()))
        }
        (2..4).forEach { num ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, num), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(1, fullCastHands.size)
    }

    @Test
    fun testComplexHand() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, nextId()))
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 4), TileStatus.NONE, nextId()))
        }
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 2), TileStatus.NONE, nextId()))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 3), TileStatus.NONE, nextId()))
        hand.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(2, fullCastHands.size)
    }

    @Test
    fun testAltComplexHand() {
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, nextId()))
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 2), TileStatus.NONE, nextId()))
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 3), TileStatus.NONE, nextId()))
        }
        repeat(2) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 4), TileStatus.NONE, nextId()))
        }
        val fullCastHands = findFullCastHand(hand)
        Assert.assertEquals(3, fullCastHands.size)
    }
}
