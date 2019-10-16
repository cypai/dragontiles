package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.Invoke
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.*

class CombatTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testCombat() {
        val flameTurtle = FlameTurtle()
        val combat = Combat(Random(),
                Hero("Elementalist", 80, 80, 15, mutableListOf(Invoke(false)), mutableListOf()),
                mutableListOf(flameTurtle))

        val controller = CombatController(combat, sEvent)
        controller.initCombat()

        // Full tile set amount is 136
        Assert.assertEquals(9, combat.openPool.size)
        Assert.assertEquals(127, combat.drawPile.size)

        runBlocking { controller.runTurn() }

        Assert.assertEquals(9, combat.openPool.size)
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(112, combat.drawPile.size)

        val invoke = controller.api.spells.first()
        Assert.assertTrue(invoke.available())
        Assert.assertFalse(invoke.ready())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle)), controller.api) }
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(30, flameTurtle.hp)

        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        Assert.assertTrue(invoke.ready())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertFalse(invoke.ready())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(28, flameTurtle.hp)
    }

    @Test
    fun testInvokePlus() {
        val flameTurtle = FlameTurtle()
        val combat = Combat(Random(),
                Hero("Elementalist", 80, 80, 15, mutableListOf(Invoke(true)), mutableListOf()),
                mutableListOf(flameTurtle))

        val controller = CombatController(combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val invoke = controller.api.spells.first()
        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        Assert.assertEquals(0, invoke.repeated)
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle)), controller.api) }
        Assert.assertTrue(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(28, flameTurtle.hp)
        Assert.assertEquals(1, invoke.repeated)

        invoke.fill(listOf(controller.api.combat.hand.first()))
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertEquals(13, combat.hand.size)
        Assert.assertEquals(26, flameTurtle.hp)
        Assert.assertEquals(2, invoke.repeated)
    }
}
