package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi

class Slime : Enemy() {

    override val strId: String = "base:enemies:Slime"
    override val assetName: String = "slime.png"

    override val hpMax: Int = 13
    override val fluxMax: Int = 0

    private var attacks: Int = 0

    override fun init(api: CombatApi) {
        attacks = api.runData.rng.nextInt(2) - 1
    }

    override suspend fun runTurn(api: CombatApi) {
    }

}
