package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import org.junit.Assert
import org.junit.Test

class TileTest {
    @Test
    fun testTileEquality() {
        val tile1 = Tile.ElementalTile(Suit.FIRE, 1)
        val tile2 = Tile.ElementalTile(Suit.FIRE, 1)
        Assert.assertNotEquals(tile1, tile2)

        val list = mutableListOf(tile1, tile2)
        list.removeAll(listOf(tile1))
        Assert.assertEquals(1, list.size)
    }
}
