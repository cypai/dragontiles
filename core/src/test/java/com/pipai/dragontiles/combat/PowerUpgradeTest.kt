package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.PowerUpgrade
import com.pipai.dragontiles.spells.common.Invoke
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class PowerUpgradeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testPowerUpgrade() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()

        runBlocking { controller.runTurn() }

        val invoke = controller.api.combat.spells.first() as Invoke
        invoke.upgrade(PowerUpgrade())

        invoke.fill(listOf(controller.api.combat.hand.first()))
        Assert.assertTrue(invoke.available())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertFalse(invoke.available())
        Assert.assertEquals(14, combat.hand.size)
        Assert.assertEquals(10, flameTurtle.hp)
        Assert.assertEquals(5, flameTurtle.flux)
    }
}
