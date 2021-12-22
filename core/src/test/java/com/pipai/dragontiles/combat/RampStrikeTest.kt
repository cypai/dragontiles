package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.baseDamage
import com.pipai.dragontiles.spells.common.Invoke
import com.pipai.dragontiles.spells.elementalist.RampStrike
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RampStrikeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testRampStrike() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(), RampStrike()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle), COMBAT_REWARDS_FIXTURE)

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val invoke = controller.api.combat.spells[0] as Invoke
        val rampStrike = controller.api.combat.spells[1]
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.requirement.find(combat.hand).first())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertEquals(6, rampStrike.baseDamage())

        runBlocking { controller.endTurn() }
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.requirement.find(combat.hand).first())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertEquals(6, rampStrike.baseDamage())
    }
}
