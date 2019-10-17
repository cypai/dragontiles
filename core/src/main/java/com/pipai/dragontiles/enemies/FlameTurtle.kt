package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.BuffCountdownAttack
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.StandardCountdownAttack
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit

class FlameTurtle : Enemy() {

    override val name: String = "Flame Turtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 30

    var attacks = 0

    override suspend fun runTurn(api: CombatApi) {
        val attack = api.fetchAttack(id)
        if (attack == null) {
            when (attacks % 3) {
                0 -> {
                    api.enemyCreateAttack(this, BuffCountdownAttack(
                            api.nextId(), 0, 8,
                            listOf(
                                    Pair(Status.DEFENSE, 2),
                                    Pair(Status.POWER, 2)),
                            listOf(this),
                            Element.NONE, Suit.LIFE,
                            "Steady",
                            "Increases @Defense and @Power by 2.",
                            1))
                }
                1 -> {
                    api.enemyCreateAttack(this, StandardCountdownAttack(
                            api.nextId(), 8, Element.FIRE, Suit.FIRE,
                            "Ember", 2))
                }
                2 -> {
                    api.enemyCreateAttack(this, StandardCountdownAttack(
                            api.nextId(), 4 + api.fetchEnemyStatus(id, Status.DEFENSE), Element.FIRE, Suit.FIRE,
                            "Shell Projection",
                            "Spell Power scales with @Defense.",
                            1))
                }
            }
        }
        attacks += 1
    }

}
