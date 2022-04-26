package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.RainbowIdentical
import com.pipai.dragontiles.spells.RainbowIdenticalSequence
import com.pipai.dragontiles.utils.withoutAll
import org.junit.Assert
import org.junit.Test

class RainbowCompReqTest {
    @Test
    fun testRainbowIdenticalSequence3() {
        val rainbow = RainbowIdenticalSequence(3)
        val hand: MutableList<TileInstance> = mutableListOf()
        (1..3).forEach { i ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, i), TileStatus.NONE, 0))
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, i), TileStatus.NONE, 0))
            hand.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, i), TileStatus.NONE, 0))
        }
        var results = rainbow.find(hand)
        Assert.assertTrue(results.size == 1)
        Assert.assertTrue(results.first().size == 9)

        results = rainbow.findGiven(
            hand.withoutAll(hand.filter { it.tile.suit == Suit.LIGHTNING }),
            hand.filter { it.tile.suit == Suit.LIGHTNING }.toList()
        )
        Assert.assertTrue(results.size == 1)
        Assert.assertTrue(results.first().size == 9)

        results = rainbow.findGiven(
            hand.withoutAll(hand.filter { (it.tile as Tile.ElementalTile).number == 2 }),
            hand.filter { (it.tile as Tile.ElementalTile).number == 2 }.toList()
        )
        Assert.assertTrue(results.size == 1)
        Assert.assertTrue(results.first().size == 9)
    }

    @Test
    fun testRainbowIdenticalSequence3_4() {
        val rainbow = RainbowIdenticalSequence(3)
        val hand: MutableList<TileInstance> = mutableListOf()
        (1..4).forEach { i ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, i), TileStatus.NONE, 0))
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, i), TileStatus.NONE, 0))
            hand.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, i), TileStatus.NONE, 0))
        }
        val results = rainbow.find(hand)
        Assert.assertTrue(results.size == 2)
        Assert.assertTrue(results.all { it.size == 9 })
    }

    @Test
    fun testRainbowIdentical3() {
        val rainbow = RainbowIdentical(3)
        val hand: MutableList<TileInstance> = mutableListOf()
        repeat(3) { _ ->
            hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 0))
            hand.add(TileInstance(Tile.ElementalTile(Suit.ICE, 1), TileStatus.NONE, 0))
            hand.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, 1), TileStatus.NONE, 0))
        }
        var results = rainbow.find(hand)
        Assert.assertTrue(results.size == 1)
        Assert.assertTrue(results.first().size == 9)

        results = rainbow.findGiven(
            hand.withoutAll(hand.filter { it.tile.suit == Suit.LIGHTNING }),
            hand.filter { it.tile.suit == Suit.LIGHTNING }.toList()
        )
        Assert.assertTrue(results.size == 1)
        Assert.assertTrue(results.first().size == 9)
    }
}
