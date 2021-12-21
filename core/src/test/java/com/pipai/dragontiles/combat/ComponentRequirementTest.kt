package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.Identical
import com.pipai.dragontiles.spells.Sequential
import com.pipai.dragontiles.spells.SequentialX
import com.pipai.dragontiles.spells.Single
import org.junit.Assert
import org.junit.Test

class ComponentRequirementTest {
    @Test
    fun testSingle() {
        val single = Single()
        val hand = mutableListOf(
            TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 0),
            TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, 1),
            TileInstance(Tile.LifeTile(LifeType.SOUL), TileStatus.NONE, 2)
        )
        val sets = single.find(hand)
        sets.forEach {
            Assert.assertEquals(1, it.size)
            Assert.assertTrue(it.first() in hand)
        }
        val slots: MutableList<TileInstance> = mutableListOf()
        Assert.assertFalse(single.satisfied(slots))
        slots.add(sets.first().first())
        Assert.assertTrue(single.satisfied(slots))

        val givenSets = single.findGiven(hand, listOf(hand.first()))
        Assert.assertEquals(2, givenSets.size)
        Assert.assertFalse(givenSets.flatten().contains(hand.first()))
    }

    @Test
    fun testIdentical3() {
        val identical = Identical(3)
        val hand = mutableListOf(
            TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 0),
            TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, 1),
            TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, 2),
            TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, 3)
        )
        val sets = identical.find(hand)
        sets.forEach {
            Assert.assertEquals(3, it.size)
            Assert.assertTrue(it.first() in hand)
        }
        val slots: MutableList<TileInstance> = mutableListOf()
        Assert.assertFalse(identical.satisfied(slots))
        slots.addAll(sets.first())
        Assert.assertTrue(identical.satisfied(slots))

        var givenSets = identical.findGiven(hand, listOf(hand.first()))
        Assert.assertTrue(givenSets.isEmpty())

        givenSets = identical.findGiven(hand, hand)
        Assert.assertTrue(givenSets.isEmpty())

        givenSets = identical.findGiven(hand, listOf(hand[1]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = identical.findGiven(hand, listOf(hand[2]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = identical.findGiven(hand, listOf(hand[3]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = identical.findGiven(hand, listOf(hand[1], hand[2]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = identical.findGiven(hand, listOf(hand[1], hand[2], hand[3]))
        Assert.assertEquals(1, givenSets.size)
    }

    @Test
    fun testSequential() {
        val sequential = Sequential(3)
        val hand = mutableListOf(
            TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 0),
            TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 1),
            TileInstance(Tile.ElementalTile(Suit.FIRE, 2), TileStatus.NONE, 2),
            TileInstance(Tile.ElementalTile(Suit.FIRE, 3), TileStatus.NONE, 3),
            TileInstance(Tile.ElementalTile(Suit.FIRE, 9), TileStatus.NONE, 4),
            TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, 5)
        )
        val sets = sequential.find(hand)
        Assert.assertEquals(2, sets.size)
        sets.forEach {
            Assert.assertEquals(3, it.size)
            Assert.assertTrue(it.first() in hand)
        }
        val slots: MutableList<TileInstance> = mutableListOf()
        Assert.assertFalse(sequential.satisfied(slots))
        slots.addAll(sets.first())
        Assert.assertTrue(sequential.satisfied(slots))

        var givenSets = sequential.findGiven(hand, listOf(hand.last()))
        Assert.assertTrue(givenSets.isEmpty())

        givenSets = sequential.findGiven(hand, listOf(hand[0]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[1]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[2]))
        Assert.assertEquals(2, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[3]))
        Assert.assertEquals(2, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[4]))
        Assert.assertTrue(givenSets.isEmpty())

        givenSets = sequential.findGiven(hand, listOf(hand[0], hand[2]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[1], hand[2]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[0], hand[2], hand[3]))
        Assert.assertEquals(1, givenSets.size)

        givenSets = sequential.findGiven(hand, listOf(hand[1], hand[2], hand[3]))
        Assert.assertEquals(1, givenSets.size)
    }

    @Test
    fun testSequentialX2() {
        val sequential = SequentialX()
        val hand = mutableListOf(
            TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 0),
            TileInstance(Tile.ElementalTile(Suit.FIRE, 2), TileStatus.NONE, 1),
            TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, 2)
        )
        val sets = sequential.find(hand)
        Assert.assertEquals(3, sets.size)
        sets.forEach {
            Assert.assertTrue(sequential.satisfied(it))
        }
    }
}
