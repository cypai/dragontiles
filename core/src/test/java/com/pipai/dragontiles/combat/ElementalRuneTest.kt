package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.common.Invoke
import com.pipai.dragontiles.spells.elementalist.ElementalRune
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ElementalRuneTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testElementalRune() {
        val flameTurtle = FlameTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(false), ElementalRune(false)), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(runData, combat, sEvent)
        controller.initCombat()
        val hand = controller.api.combat.hand
        hand.clear()
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), 1))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), 2))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 2), 3))

        runBlocking { controller.runTurn() }

        val invoke = controller.api.combat.spells[0] as Invoke
        val elementalRune = controller.api.combat.spells[1] as ElementalRune

        elementalRune.fill(hand.subList(0, 2))
        runBlocking { elementalRune.activate(controller.api) }

        invoke.fill(listOf(hand[2]))
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.id)), controller.api) }
        Assert.assertEquals(26, flameTurtle.hp)
    }
}
