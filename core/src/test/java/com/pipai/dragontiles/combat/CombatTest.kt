package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.common.Invoke
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class CombatTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testCombat() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(false)), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()

        // Full tile set amount is 136
        Assert.assertEquals(9, combat.openPool.size)
        Assert.assertEquals(127, combat.drawPile.size)

        runBlocking { controller.runTurn() }

        Assert.assertEquals(9, combat.openPool.size)
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(112, combat.drawPile.size)

        val invoke = controller.api.spells.first() as Invoke
        Assert.assertTrue(invoke.available())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(30, flameTurtle.hp)

        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(28, flameTurtle.hp)
    }

    @Test
    fun testInvokePlus() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(true)), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val invoke = controller.api.spells.first() as Invoke
        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        Assert.assertEquals(0, invoke.repeated)
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertTrue(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(28, flameTurtle.hp)
        Assert.assertEquals(1, invoke.repeated)

        invoke.fill(listOf(controller.api.combat.hand.first()))
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertEquals(13, combat.hand.size)
        Assert.assertEquals(26, flameTurtle.hp)
        Assert.assertEquals(2, invoke.repeated)
    }
}
