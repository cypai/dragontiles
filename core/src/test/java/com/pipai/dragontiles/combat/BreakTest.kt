package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.common.Invoke
import com.pipai.dragontiles.status.BreakStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class BreakTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testBreak() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle), COMBAT_REWARDS_FIXTURE)

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        Assert.assertEquals(2, controller.api.calculateDamageOnEnemy(flameTurtle, Element.FIRE, 2))

        runBlocking { controller.api.addStatusToEnemy(flameTurtle, BreakStatus(1, false)) }

        Assert.assertEquals(3, controller.api.calculateDamageOnEnemy(flameTurtle, Element.FIRE, 2))

        Assert.assertEquals(1, controller.api.enemyStatusAmount(flameTurtle, BreakStatus::class))

        runBlocking { controller.endTurn() }

        Assert.assertEquals(0, controller.api.enemyStatusAmount(flameTurtle, BreakStatus::class))
        Assert.assertFalse(controller.api.enemyHasStatus(flameTurtle, BreakStatus::class))
    }
}
