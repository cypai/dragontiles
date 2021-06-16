package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi

abstract class Enemy {

    abstract val strId: String
    abstract val assetName: String

    abstract val hpMax: Int
    var hp: Int = 0
    abstract val fluxMax: Int
    var flux: Int = 0
    var id: Int = 0

    fun preInit(id: Int) {
        hp = hpMax
        this.id = id
    }

    open fun init(api: CombatApi) {
    }

    abstract suspend fun runTurn(api: CombatApi)

}
