package com.pipai.dragontiles.data

import org.junit.Assert
import org.junit.Test

class OrphanTest {
    @Test
    fun testTerminal() {
        Assert.assertTrue(
            orphan(
                Tile.ElementalTile(Suit.ICE, 1),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 1),
                )
            )
        )

        Assert.assertTrue(
            orphan(
                Tile.ElementalTile(Suit.ICE, 1),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 1),
                    Tile.ElementalTile(Suit.ICE, 3),
                )
            )
        )

        Assert.assertFalse(
            orphan(
                Tile.ElementalTile(Suit.ICE, 1),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 1),
                    Tile.ElementalTile(Suit.ICE, 1),
                )
            )
        )

        Assert.assertFalse(
            orphan(
                Tile.ElementalTile(Suit.ICE, 1),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 1),
                    Tile.ElementalTile(Suit.ICE, 2),
                )
            )
        )

        Assert.assertTrue(
            orphan(
                Tile.ElementalTile(Suit.ICE, 1),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 9),
                    Tile.ElementalTile(Suit.ICE, 1),
                )
            )
        )

        Assert.assertTrue(
            orphan(
                Tile.ElementalTile(Suit.ICE, 9),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 9),
                )
            )
        )

        Assert.assertTrue(
            orphan(
                Tile.ElementalTile(Suit.ICE, 9),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 9),
                    Tile.ElementalTile(Suit.ICE, 7),
                )
            )
        )

        Assert.assertFalse(
            orphan(
                Tile.ElementalTile(Suit.ICE, 9),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 9),
                    Tile.ElementalTile(Suit.ICE, 9),
                )
            )
        )

        Assert.assertFalse(
            orphan(
                Tile.ElementalTile(Suit.ICE, 9),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 9),
                    Tile.ElementalTile(Suit.ICE, 8),
                )
            )
        )

        Assert.assertTrue(
            orphan(
                Tile.ElementalTile(Suit.ICE, 9),
                listOf(
                    Tile.ElementalTile(Suit.ICE, 9),
                    Tile.ElementalTile(Suit.ICE, 1),
                )
            )
        )
    }
}
