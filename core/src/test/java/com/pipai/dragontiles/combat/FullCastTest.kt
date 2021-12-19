package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.StarType
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.sorceries.findFullCastHand
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
                hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, num), nextId()))
            }
        }
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 4), nextId()))
        }
        repeat(3) {
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, 9), nextId()))
        }
        repeat(2) {
            hand.add(TileInstance(Tile.StarTile(StarType.STAR), nextId()))
        }
        val fullCastHands = findFullCastHand(hand, hand.size)
        Assert.assertEquals(1, fullCastHands.size)
    }
}
