package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.Invoke
import com.pipai.dragontiles.spells.RampStrike
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RampStrikeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testRampStrike() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(false), RampStrike(false)), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val invoke = controller.api.spells[0]
        val rampStrike = controller.api.spells[1]
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.requirement.find(combat.hand).first())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle)), controller.api) }
        Assert.assertEquals(5, rampStrike.baseDamage())

        runBlocking { controller.endTurn() }
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.requirement.find(combat.hand).first())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle)), controller.api) }
        Assert.assertEquals(5, rampStrike.baseDamage())
    }
}
