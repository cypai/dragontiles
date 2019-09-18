package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Element

class FlameTurtle : Enemy() {

    override val name: String = "Flame Turtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 30

    override fun runTurn(api: CombatApi) {
        api.enemyAttack(this,
                CountdownAttack(api.nextId(), this.id, 8, 1, Element.FIRE, 2, "", ""))
    }

}
