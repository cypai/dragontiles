package com.pipai.dragontiles.combat

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
    val openPile: MutableList<Tile> = mutableListOf()
}

class CombatController(private val combat: Combat) {

    private val api: CombatApi = CombatApi(combat)

    fun initCombat() {
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
        repeat(9) {
            combat.openPile.add(combat.drawPile.removeAt(0))
        }
    }

    fun runTurn() {
        combat.turnNumber += 1
        combat.spellsCasted = 0
        combat.enemies.forEach {
            it.runTurn(api)
        }
        while (combat.hand.size < combat.hero.handSize) {
            api.draw()
        }
    }

}