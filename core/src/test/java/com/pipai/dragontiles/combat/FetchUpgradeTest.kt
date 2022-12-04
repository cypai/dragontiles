package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.upgrades.ScryUpgrade
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ScryUpgradeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testScryCanUpgrade() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()

        runBlocking { controller.runTurn() }

        val invoke = controller.api.combat.spells.first() as Invoke
        Assert.assertTrue(ScryUpgrade().canUpgrade(invoke))
    }
}
