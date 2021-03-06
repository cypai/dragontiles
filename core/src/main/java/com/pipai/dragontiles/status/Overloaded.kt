package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.EnemyTurnEndEvent

class Overloaded(amount: Int) : Status(amount) {
    override val strId = "base:status:Overloaded"
    override val assetName = "assets/binassets/graphics/status/overloaded.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Overloaded(amount)
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        amount--
        if (amount == 0) {
            when (val c = combatant) {
                is Combatant.HeroCombatant -> api.heroLoseFlux(api.runData.hero.fluxMax / 2)
                is Combatant.EnemyCombatant -> api.enemyLoseFlux(c.enemy, c.enemy.fluxMax / 2)
            }
        }
        api.notifyStatusUpdated()
    }
}
