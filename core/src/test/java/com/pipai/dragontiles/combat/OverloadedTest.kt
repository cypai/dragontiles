package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.elementalist.RampStrike
import com.pipai.dragontiles.status.Overloaded
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class OverloadedTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testPlayerOverload() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(), RampStrike()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle), COMBAT_REWARDS_FIXTURE)

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        runBlocking { controller.api.dealFluxDamageToHero(100) }
        Assert.assertTrue(controller.api.heroHasStatus(Overloaded::class))

        runBlocking { controller.endTurn() }
        Assert.assertTrue(controller.api.heroHasStatus(Overloaded::class))
        Assert.assertTrue(runData.hero.hp < runData.hero.hpMax)

        runBlocking { controller.endTurn() }
        Assert.assertFalse(controller.api.heroHasStatus(Overloaded::class))
        Assert.assertEquals(runData.hero.fluxMax / 2, runData.hero.flux)
    }

    @Test
    fun testEnemyOverload() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(), RampStrike()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle), COMBAT_REWARDS_FIXTURE)

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        runBlocking { controller.api.dealFluxDamageToEnemy(flameTurtle, 100) }
        Assert.assertTrue(controller.api.enemyHasStatus(flameTurtle, Overloaded::class))

        runBlocking { controller.endTurn() }
        Assert.assertTrue(controller.api.enemyHasStatus(flameTurtle, Overloaded::class))

        runBlocking { controller.endTurn() }
        Assert.assertFalse(controller.api.enemyHasStatus(flameTurtle, Overloaded::class))
        Assert.assertEquals(flameTurtle.fluxMax / 2, flameTurtle.flux)
    }
}
