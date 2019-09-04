package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.spells.Invoke
import com.pipai.test.fixtures.mockCombatWorld
import com.pipai.test.libgdx.GdxMockedTest
import org.junit.Assert
import org.junit.Test
import java.util.*

class CombatTest : GdxMockedTest() {
    @Test
    fun testCombatInit() {
        val combat = Combat(Random(),
                Hero("Elementalist", 80, 80, 15, mutableListOf(Invoke())),
                mutableListOf(FlameTurtle()))

        val controller = CombatController(combat, mockCombatWorld(combat))
        controller.initCombat()

        // Full tile set amount is 136
        Assert.assertEquals(9, combat.openPool.size)
        Assert.assertEquals(127, combat.drawPile.size)

        controller.runTurn()
        Assert.assertEquals(9, combat.openPool.size)
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(112, combat.drawPile.size)
    }
}
