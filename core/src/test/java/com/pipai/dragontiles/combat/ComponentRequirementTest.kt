package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.Identical
import com.pipai.dragontiles.spells.Single
import com.pipai.dragontiles.spells.generateSlots
import org.junit.Assert
import org.junit.Test

class ComponentRequirementTest {
    @Test
    fun testSingle() {
        val single = Single()
        val hand = mutableListOf(
                TileInstance(Tile.ElementalTile(Suit.FIRE, 1), 0),
                TileInstance(Tile.StarTile(StarType.STAR), 1),
                TileInstance(Tile.LifeTile(LifeType.SOUL), 2)
        )
        val sets = single.find(hand)
        sets.forEach {
            Assert.assertEquals(1, it.size)
            Assert.assertTrue(it.first() in hand)
        }
        val slots = generateSlots(1)
        Assert.assertFalse(single.satisfied(slots))
        slots.first().tile = sets.first().first()
        Assert.assertTrue(single.satisfied(slots))
    }

    @Test
    fun testIdentical() {
        val identical = Identical(3)
        val hand = mutableListOf(
                TileInstance(Tile.ElementalTile(Suit.FIRE, 1), 0),
                TileInstance(Tile.StarTile(StarType.STAR), 1),
                TileInstance(Tile.StarTile(StarType.STAR), 2),
                TileInstance(Tile.StarTile(StarType.STAR), 3)
        )
        val sets = identical.find(hand)
        sets.forEach {
            Assert.assertEquals(3, it.size)
            Assert.assertTrue(it.first() in hand)
        }
        val slots = generateSlots(3)
        Assert.assertFalse(identical.satisfied(slots))
        slots.zip(sets.first()) { slot, tile ->
            slot.tile = tile
        }
        Assert.assertTrue(identical.satisfied(slots))
    }
}
