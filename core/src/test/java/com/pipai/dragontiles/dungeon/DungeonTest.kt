package com.pipai.dragontiles.dungeon

import org.junit.Assert
import org.junit.Test
import java.util.*

class DungeonTest {
    @Test
    fun testMapGeneration() {
        val plainsDungeon = PlainsDungeon()
        plainsDungeon.generateMap(Random())
        val map = plainsDungeon.getMap()
        map.forEachIndexed { index, floor ->
            when (index) {
                10 -> {
                    Assert.assertEquals(1, floor.size)
                    val node = floor.first()
                    Assert.assertEquals(MapNodeType.BOSS, node.type)
                    Assert.assertFalse(node.hidden)
                    Assert.assertTrue(node.next.isEmpty())
                    Assert.assertFalse(node.prev.isEmpty())

                }
                9 -> {
                    Assert.assertEquals(1, floor.size)
                    val node = floor.first()
                    Assert.assertEquals(MapNodeType.TOWN, node.type)
                    Assert.assertFalse(node.hidden)
                    Assert.assertFalse(node.next.isEmpty())
                    Assert.assertFalse(node.prev.isEmpty())
                }
                1 -> {
                    floor.forEach { node ->
                        Assert.assertEquals(MapNodeType.COMBAT, node.type)
                        Assert.assertFalse(node.hidden)
                        Assert.assertFalse(node.next.isEmpty())
                        Assert.assertEquals(1, node.prev.size)
                    }
                }
                0 -> {
                    Assert.assertEquals(1, floor.size)
                    val node = floor.first()
                    Assert.assertEquals(MapNodeType.NOOP, node.type)
                    Assert.assertFalse(node.next.isEmpty())
                    Assert.assertTrue(node.prev.isEmpty())
                }
                else -> {
                    floor.forEach {
                        Assert.assertFalse(it.next.isEmpty())
                        Assert.assertFalse(it.prev.isEmpty())
                    }
                }
            }
        }
    }
}
