package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Spell

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

    val heroStatus = StatusData()
    val enemyStatus: MutableMap<Int, StatusData> = mutableMapOf()
}

data class Status(val strId: String, val decreasing: Boolean) {
    companion object {
        val STRENGTH = Status("base:status:Power", false)
        val DEFENSE = Status("base:status:Defense", false)
        val FIRE_BREAK = Status("base:status:FireBreak", true)
        val ICE_BREAK = Status("base:status:IceBreak", true)
        val LIGHTNING_BREAK = Status("base:status:LightningBreak", true)
        val NONELEMENTAL_BREAK = Status("base:status:NonElementalBreak", true)
    }
}

data class StatusData(val data: MutableMap<Status, Int> = mutableMapOf()) {

    operator fun get(status: Status): Int {
        return data[status] ?: 0
    }

    operator fun set(status: Status, value: Int) {
        data[status] = value
    }

    fun clear() {
        data.clear()
    }

    fun remove(status: Status): Int? {
        return data.remove(status)
    }

    fun has(status: Status): Boolean {
        return data[status] ?: 0 > 0
    }

    fun pairs(): List<Pair<Status, Int>> {
        return data.entries.map { it.toPair() }
    }

    fun increment(status: Status, amount: Int) {
        data[status] = (data[status] ?: 0) + amount
    }

    fun decrementAll() {
        data.filter { it.key.decreasing }
            .forEach { increment(it.key, -1) }
    }
}
