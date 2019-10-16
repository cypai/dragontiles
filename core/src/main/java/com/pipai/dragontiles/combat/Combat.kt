package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy

data class Combat(val enemies: MutableList<Enemy>) {

    var turnNumber = 0

    val hand: MutableList<TileInstance> = mutableListOf()
    val drawPile: MutableList<TileInstance> = mutableListOf()
    val discardPile: MutableList<TileInstance> = mutableListOf()
    val openPool: MutableList<TileInstance> = mutableListOf()

    val enemyAttacks: MutableMap<Int, CountdownAttack> = mutableMapOf()

    val heroStatus = StatusData()
    val enemyStatus: MutableMap<Int, StatusData> = mutableMapOf()
}

enum class Status(val decreasing: Boolean, val positive: Boolean) {
    POWER(false, false),
    FIRE_BREAK(true, true),
    ICE_BREAK(true, true),
    LIGHTNING_BREAK(true, true)
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

    fun has(status: Status): Boolean {
        return data[status] ?: 0 > 0
    }

    fun increment(status: Status, amount: Int) {
        data[status] = data[status] ?: 0 + amount
    }

    fun decrementAll() {
        data.filter { it.key.decreasing }
                .forEach { increment(it.key, -1) }
    }
}
