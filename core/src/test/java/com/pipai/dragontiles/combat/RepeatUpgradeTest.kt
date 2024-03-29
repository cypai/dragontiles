package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.upgrades.RepeatUpgrade
import com.pipai.dragontiles.spells.colorless.Invoke
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RepeatUpgradeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testPowerUpgrade() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()

        runBlocking { controller.runTurn() }

        val invoke = controller.api.combat.spells.first() as Invoke
        invoke.upgrade(RepeatUpgrade())

        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertTrue(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(10, flameTurtle.hp)
        Assert.assertEquals(2, flameTurtle.flux)

        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(10, flameTurtle.hp)
        Assert.assertEquals(4, flameTurtle.flux)
    }
}
