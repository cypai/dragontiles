package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.common.Invoke
import com.pipai.dragontiles.spells.elementalist.EnhanceRune
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class EnhanceRuneTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testEnhanceRune() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(false), EnhanceRune(false)), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val hand = controller.api.combat.hand
        hand.clear()
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), 1))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 2), 2))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 3), 3))

        val invoke = controller.api.combat.spells[0] as Invoke
        val enhanceRune = controller.api.combat.spells[1] as EnhanceRune

        enhanceRune.fill(hand.subList(0, 2))
        runBlocking { enhanceRune.activate(controller.api) }
        // Runes remove tile from hand -> assigned
        Assert.assertEquals(1, hand.size)
        Assert.assertEquals(2, combat.assigned[1]?.size)

        invoke.fill(listOf(hand[0]))
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertEquals(26, flameTurtle.hp)
    }
}
