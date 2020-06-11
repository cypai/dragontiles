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

    val enemyAttacks: MutableMap<Int, CountdownAttack> = mutableMapOf()

    val heroStatus = StatusData()
    val enemyStatus: MutableMap<Int, StatusData> = mutableMapOf()
}

enum class Status(val strId: String, val decreasing: Boolean) {
    STRENGTH("base:status:Power", false),
    DEFENSE("base:status:Defense", false),
    FIRE_BREAK("base:status:FireBreak", true),
    ICE_BREAK("base:status:IceBreak", true),
    LIGHTNING_BREAK("base:status:LightningBreak", true),
    NONELEMENTAL_BREAK("base:status:NonElementalBreak", true),
    DRAGON_RAGE("base:status:DragonRage", false),
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
