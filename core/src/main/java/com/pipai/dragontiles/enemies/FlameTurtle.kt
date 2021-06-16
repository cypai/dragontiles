package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi

class FlameTurtle : Enemy() {

    override val strId: String = "base:enemies:FlameTurtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 10
    override val fluxMax: Int = 30

    override suspend fun runTurn(api: CombatApi) {
    }

}
