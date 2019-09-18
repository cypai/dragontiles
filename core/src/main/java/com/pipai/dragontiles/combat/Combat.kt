package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.CountdownAttack
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

    val incomingAttacks: MutableList<CountdownAttack> = mutableListOf()

}
