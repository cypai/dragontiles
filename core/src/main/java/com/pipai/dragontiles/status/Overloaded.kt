package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.EnemyTurnEndEvent

class Overloaded(amount: Int) : Status(amount) {
    override val id = "base:status:Overloaded"
    override val assetName = "overloaded.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Overloaded(amount)
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        amount--
        if (amount == 0) {
            api.notifyStatusUpdated()
            when (val c = combatant!!) {
                is Combatant.HeroCombatant -> {
                    api.heroLoseFlux(api.runData.hero.tempFluxMax / 2)
                }
                is Combatant.EnemyCombatant -> api.enemyLoseFlux(c.enemy, c.enemy.fluxMax / 2)
            }
        }
    }
}
