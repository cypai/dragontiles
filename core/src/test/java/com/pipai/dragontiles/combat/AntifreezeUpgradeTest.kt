package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.colorless.Bump
import com.pipai.dragontiles.spells.upgrades.AntifreezeUpgrade
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class AntifreezeUpgradeTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testAntifreezeUpgrade() {
        val largeTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Bump()), mutableListOf())
        val combat = Combat(mutableListOf(largeTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()

        runBlocking { controller.runTurn() }

        val bump = controller.api.combat.spells.first() as Bump
        val tile = controller.api.combat.hand.first()
        tile.tileStatus = TileStatus.FREEZE
        bump.fill(listOf(tile))
        Assert.assertFalse(bump.requirement.satisfied(bump.components()))

        bump.upgrade(AntifreezeUpgrade())
        Assert.assertTrue(bump.requirement.satisfied(bump.components()))
    }
}
