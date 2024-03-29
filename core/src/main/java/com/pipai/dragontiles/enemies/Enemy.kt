package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.AssetConfig
import com.pipai.dragontiles.data.AssetType
import com.pipai.dragontiles.data.Localized

abstract class Enemy : Localized {

    abstract val assetName: String
    open val assetConfig: AssetConfig = AssetConfig(AssetType.SPRITE, 2f)

    abstract val hpMax: Int
    var hp: Int = 0
    abstract val fluxMax: Int
    var flux: Int = 0
    var enemyId: Int = 0
    var x: Float = 0f
        private set
    var y: Float = 0f
        private set

    fun at(x: Float, y: Float): Enemy {
        this.x = x
        this.y = y
        return this
    }

    fun preInit(id: Int) {
        hp = hpMax
        this.enemyId = id
    }

    open suspend fun init(api: CombatApi) {
    }

    abstract fun getIntent(api: CombatApi): Intent

    abstract fun nextIntent(api: CombatApi): Intent
}
