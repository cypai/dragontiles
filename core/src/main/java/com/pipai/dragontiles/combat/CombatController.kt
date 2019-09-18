package com.pipai.dragontiles.combat

import com.artemis.World
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.data.LifeType
import com.pipai.dragontiles.data.StarType
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile

class CombatController(private val combat: Combat, world: World, game: DragonTilesGame) {

    val api: CombatApi = CombatApi(combat, combat.hero.spells.map { it.createInstance() }.toList(), world, game)

    fun initCombat() {
        Tile.nextId = 0
        combat.enemies.forEach {
            it.preInit(api.nextId())
            it.init()
        }
        initDrawPile()
        initOpenPile()
    }

    private fun initDrawPile() {
        for (i in 1..9) {
            repeat(4) {
                combat.drawPile.add(Tile.ElementalTile(Suit.FIRE, i))
                combat.drawPile.add(Tile.ElementalTile(Suit.ICE, i))
                combat.drawPile.add(Tile.ElementalTile(Suit.LIGHTNING, i))
            }
        }
        repeat(4) {
            combat.drawPile.add(Tile.StarTile(StarType.EARTH))
            combat.drawPile.add(Tile.StarTile(StarType.MOON))
            combat.drawPile.add(Tile.StarTile(StarType.SUN))
            combat.drawPile.add(Tile.StarTile(StarType.STAR))
        }
        repeat(4) {
            combat.drawPile.add(Tile.LifeTile(LifeType.LIFE))
            combat.drawPile.add(Tile.LifeTile(LifeType.MIND))
            combat.drawPile.add(Tile.LifeTile(LifeType.SOUL))
        }
        combat.drawPile.shuffle(combat.rng)
    }

    private fun initOpenPile() {
        api.drawToOpenPool(9)
        api.sortOpenPool()
    }

    fun runTurn() {
        combat.turnNumber += 1
        combat.spellsCasted = 0
        combat.enemies.forEach {
            it.runTurn(api)
        }
        if (combat.hero.handSize > combat.hand.size) {
            api.draw(combat.hero.handSize - combat.hand.size)
        }
        api.sortHand()
    }

    fun endTurn() {
        combat.incomingAttacks.toList().forEach {
            api.updateCountdownAttack(it)
        }
        runTurn()
    }

}
