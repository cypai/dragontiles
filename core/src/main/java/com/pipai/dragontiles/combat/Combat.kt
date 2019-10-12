package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.hero.Hero
import java.util.*

data class Combat(val rng: Random,
                  val hero: Hero,
                  val enemies: MutableList<Enemy>) {

    var turnNumber = 0

    val hand: MutableList<TileInstance> = mutableListOf()
    val drawPile: MutableList<TileInstance> = mutableListOf()
    val discardPile: MutableList<TileInstance> = mutableListOf()
    val openPool: MutableList<TileInstance> = mutableListOf()

    val incomingAttacks: MutableList<CountdownAttack> = mutableListOf()

    val heroStatus: MutableMap<Status, Int> = mutableMapOf()
    val enemyStatus: MutableMap<Int, MutableMap<Status, Int>> = mutableMapOf()
}

enum class Status {
    POWER, FIRE_BREAK, ICE_BREAK, LIGHTNING_BREAK
}
