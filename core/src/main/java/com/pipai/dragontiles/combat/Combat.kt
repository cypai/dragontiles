package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.status.Status

data class Combat(val enemies: List<Enemy>) {

    var turnNumber = 0

    val spells: MutableList<Spell> = mutableListOf()

    val hand: MutableList<TileInstance> = mutableListOf()
    val drawPile: MutableList<TileInstance> = mutableListOf()
    val discardPile: MutableList<TileInstance> = mutableListOf()
    val openPool: MutableList<TileInstance> = mutableListOf()

    /**
     * StandardSpell Index -> List<TileInstance> in CombatApi
     * For assigned tiles in runes
     */
    val assigned: MutableMap<Int, List<TileInstance>> = mutableMapOf()

    val heroStatus: MutableList<Status> = mutableListOf()
    val enemyStatus: MutableMap<Int, MutableList<Status>> = mutableMapOf()
}
