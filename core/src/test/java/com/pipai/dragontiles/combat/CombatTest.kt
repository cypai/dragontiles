package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.colorless.Invoke
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class CombatTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testCombat() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()

        // Full tile set amount is 136
        Assert.assertEquals(9, combat.pool.size)
        Assert.assertEquals(127, combat.drawPile.size)

        runBlocking { controller.runTurn() }

        Assert.assertEquals(9, combat.pool.size)
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(112, combat.drawPile.size)

        // Test cast attempt without proper fill
        val invoke = controller.api.combat.spells.first() as Invoke
        Assert.assertTrue(invoke.available())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertEquals(15, combat.hand.size)
        Assert.assertEquals(10, flameTurtle.hp)
        Assert.assertEquals(0, flameTurtle.flux)

        // Properly filled
        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(10, flameTurtle.hp)
        Assert.assertEquals(2, flameTurtle.flux)
    }
}
