package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.Localized

abstract class Enemy : Localized {

    abstract val assetName: String

    abstract val hpMax: Int
    var hp: Int = 0
    abstract val fluxMax: Int
    var flux: Int = 0
    var enemyId: Int = 0

    fun preInit(id: Int) {
        hp = hpMax
        this.enemyId = id
    }

    open suspend fun init(api: CombatApi) {
    }

    abstract fun getIntent(api: CombatApi): Intent

    abstract fun nextIntent(api: CombatApi): Intent
}
