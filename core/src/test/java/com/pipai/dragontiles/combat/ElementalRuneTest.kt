package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.colorless.Invoke
import com.pipai.dragontiles.spells.elementalist.ElementalRune
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ElementalRuneTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testElementalRune() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Invoke(), ElementalRune()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()
        runBlocking { controller.runTurn() }

        val hand = controller.api.combat.hand
        hand.clear()
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 1))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 2))
        hand.add(TileInstance(Tile.ElementalTile(Suit.FIRE, 1), TileStatus.NONE, 3))

        val invoke = controller.api.combat.spells[0] as Invoke
        val elementalRune = controller.api.combat.spells[1] as ElementalRune

        elementalRune.fill(hand.subList(0, 2))
        runBlocking { elementalRune.activate(controller.api) }
        // Runes remove tile from hand -> assigned
        Assert.assertEquals(1, hand.size)
        Assert.assertEquals(2, combat.assigned[1]?.size)

        invoke.fill(listOf(hand[0]))
        runBlocking { invoke.cast(CastParams(listOf(flameTurtle.enemyId)), controller.api) }
        Assert.assertEquals(4, flameTurtle.flux)
    }
}
