package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.StarType
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.enemies.LargeTurtle
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.elementalist.Vent
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class VentTest : CombatBackendTest(QueryHandler()) {
    @Test
    fun testVent() {
        val flameTurtle = LargeTurtle()
        val runData = runDataFixture(mutableListOf(Vent()), mutableListOf())
        val combat = Combat(mutableListOf(flameTurtle))

        val controller = CombatController(gameData, runData, combat, sEvent)
        controller.init()
        controller.initCombat()

        runBlocking { controller.runTurn() }

        val hand = controller.api.combat.hand
        hand.clear()
        hand.add(TileInstance(Tile.StarTile(StarType.EARTH), TileStatus.NONE, 1))
        hand.add(TileInstance(Tile.StarTile(StarType.MOON), TileStatus.NONE, 2))

        runBlocking { controller.api.dealFluxDamageToHero(12) }

        val vent = controller.api.combat.spells.first() as Vent

        vent.fill(hand.subList(0, 2))
        runBlocking { vent.cast(CastParams(listOf()), controller.api) }
        Assert.assertEquals(5, runData.hero.flux)
    }
}
