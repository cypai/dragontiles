package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.EnemyTurnStartEvent

class Cryoshock(amount: Int) : Status(amount) {
    override val id = "base:status:Cryoshock"
    override val assetName = "cryoshock.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Cryoshock(amount)
    }

    @CombatSubscribe
    suspend fun onEnemyStart(ev: EnemyTurnStartEvent, api: CombatApi) {
        val total = api.combat.enemies
            .filter { it.hp > 0 }
            .map { api.enemyStatusAmount(it, Cryoshock::class) }
            .sum()
        when (val c = combatant) {
            is Combatant.EnemyCombatant -> {
                api.dealDamageToEnemy(c.enemy, total)
            }
            else -> {
            }
        }
    }
}
