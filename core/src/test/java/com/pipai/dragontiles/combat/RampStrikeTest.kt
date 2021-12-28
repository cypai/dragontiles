package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.baseDamage
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.elementalist.RampStrike
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RampStrikeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testRampStrike() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(), RampStrike()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val invoke = controller.api.combat.spells[0] as Invoke
        val rampStrike = controller.api.combat.spells[1]
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.requirement.find(combat.hand).first())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertEquals(6, rampStrike.baseDamage())

        runBlocking { controller.endTurn() }
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.requirement.find(combat.hand).first())
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertEquals(6, rampStrike.baseDamage())
    }
}
