package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi

class FlameTurtle : Enemy() {

    override val name: String = "Flame Turtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 30

    override fun runTurn(api: CombatApi) {

    }

}
