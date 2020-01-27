package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.BuffCountdownAttack
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.StandardCountdownAttack
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit

class FlameTurtle : Enemy() {

    override val strId: String = "base:enemies:FlameTurtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 30

    private var attacks = 0

    override suspend fun runTurn(api: CombatApi) {
        val attack = api.fetchAttack(id)
        if (attack == null) {
            when (attacks % 3) {
                0 -> {
                    api.enemyCreateAttack(this, BuffCountdownAttack(
                            api.nextId(), 0, 8,
                            listOf(
                                    Pair(Status.DEFENSE, 1),
                                    Pair(Status.STRENGTH, 1)),
                            listOf(this),
                            Element.NONE, Suit.LIFE,
                            "base:enemies:FlameTurtle:Steady",
                            1))
                }
                1 -> {
                    api.enemyCreateAttack(this, StandardCountdownAttack(
                            api.nextId(), 8, Element.FIRE, Suit.FIRE,
                            "base:enemies:FlameTurtle:Ember", 2))
                }
                2 -> {
                    api.enemyCreateAttack(this, StandardCountdownAttack(
                            api.nextId(), 4 + api.fetchEnemyStatus(id, Status.DEFENSE), Element.FIRE, Suit.FIRE,
                            "base:enemies:FlameTurtle:ShellProjection",
                            1))
                }
            }
        }
        attacks += 1
    }

}
