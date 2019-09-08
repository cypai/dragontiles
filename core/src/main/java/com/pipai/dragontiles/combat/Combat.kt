package com.pipai.dragontiles.combat

import com.artemis.World
import com.pipai.dragontiles.data.LifeType
import com.pipai.dragontiles.data.StarType
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.hero.Hero
import java.util.*

data class Combat(val rng: Random,
                  val hero: Hero,
                  val enemies: MutableList<Enemy>) {

    var turnNumber = 0
    var spellsCasted = 0

    val hand: MutableList<Tile> = mutableListOf()
    val drawPile: MutableList<Tile> = mutableListOf()
    val discardPile: MutableList<Tile> = mutableListOf()
    val openPool: MutableList<Tile> = mutableListOf()
}

class CombatController(private val combat: Combat, world: World) {

    val api: CombatApi = CombatApi(combat, combat.hero.spells.map { it.createInstance() }.toList(), world)

    fun initCombat() {
        Tile.nextId = 0
        combat.enemies.forEach {
            it.preInit()
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

}
