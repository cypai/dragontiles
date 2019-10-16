package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.StandardCountdownAttack
import com.pipai.dragontiles.data.Element

class FlameTurtle : Enemy() {

    override val name: String = "Flame Turtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 30

    override suspend fun runTurn(api: CombatApi) {
        val attack = api.fetchAttack(id)
        if (attack == null) {
            api.enemyCreateAttack(this, StandardCountdownAttack(
                    api.nextId(), 8, Element.FIRE, "Ember", 2))
        } else {
            api.countdownAttackTick(id)
        }
    }

}
