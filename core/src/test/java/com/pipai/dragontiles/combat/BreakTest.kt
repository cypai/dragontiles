package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.spells.common.Invoke
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class BreakTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testRampStrike() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(false)), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        Assert.assertEquals(1, controller.api.calculateTargetEnemyDamage(flameTurtle, Element.FIRE, 1))

        controller.api.changeEnemyStatusIncrement(flameTurtle.id, Status.FIRE_BREAK, 1)

        Assert.assertEquals(2, controller.api.calculateTargetEnemyDamage(flameTurtle, Element.FIRE, 1))
    }
}
