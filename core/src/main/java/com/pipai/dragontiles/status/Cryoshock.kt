package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*

class Cryoshock(amount: Int) : Status(amount) {
    override val id = "base:status:Cryoshock"
    override val assetName = "cryoshock.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Cryoshock(amount)
    }

    @CombatSubscribe
    suspend fun onEnemyStart(ev: EnemyTurnStartEvent, api: CombatApi) {
        when (val c = combatant) {
            is Combatant.EnemyCombatant -> {
                api.dealDamageToEnemy(c.enemy, amount)
            }
            else -> {
            }
        }
    }
}
