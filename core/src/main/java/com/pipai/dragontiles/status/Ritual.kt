package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.EnemyTurnEndEvent

class Ritual(amount: Int) : Status(amount) {
    override val id = "base:status:Ritual"
    override val assetName = "ritual.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Ritual(amount)
    }

    @CombatSubscribe
    suspend fun onEnemyEndTurn(ev: EnemyTurnEndEvent, api: CombatApi) {
        when (val c = combatant) {
            is Combatant.HeroCombatant -> {
                api.addStatusToHero(Strength(1))
            }
            is Combatant.EnemyCombatant -> {
                api.addStatusToEnemy(c.enemy, Strength(1))
            }
            else -> {
            }
        }
    }
}
