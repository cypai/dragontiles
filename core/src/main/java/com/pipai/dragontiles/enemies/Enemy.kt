package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi

abstract class Enemy {

    abstract val name: String

    abstract val hpMax: Int
    var hp: Int = 0

    fun preInit() {
        hp = hpMax
    }

    open fun init() {
    }

    abstract fun runTurn(api: CombatApi)

}
