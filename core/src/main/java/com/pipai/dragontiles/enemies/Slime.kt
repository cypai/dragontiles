package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit

class Slime : Enemy() {

    override val strId: String = "base:enemies:Slime"
    override val assetName: String = "slime.png"

    override val hpMax: Int = 13

    private var attacks: Int = 0

    override fun init(api: CombatApi) {
        attacks = api.runData.rng.nextInt(2) - 1
    }

    override suspend fun runTurn(api: CombatApi) {
        val attack = api.fetchAttack(id)
        if (attack == null) {
            when (attacks % 4) {
                0 -> {
                    api.enemyCreateAttack(this, DebuffCountdownAttack(
                            api.nextId(), 0, 8,
                            listOf(Pair(Status.ICE_BREAK, 3)),
                            Element.ICE, Suit.ICE,
                            "base:enemies:Slime:Acid", 1))
                }
                1 -> {
                    api.enemyCreateAttack(this, StandardCountdownAttack(
                            api.nextId(), 8, Element.ICE, Suit.ICE,
                            "base:enemies:standard:IceStrike", 2))
                }
                else -> {
                    api.enemyCreateAttack(this, StandardCountdownAttack(
                            api.nextId(), 3, Element.ICE, Suit.ICE,
                            "base:enemies:standard:Frost", 1))
                }
            }
        }
        attacks += 1
    }

}
