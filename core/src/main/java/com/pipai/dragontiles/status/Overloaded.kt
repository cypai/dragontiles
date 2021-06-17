package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.EnemyTurnEndEvent

class Overloaded(amount: Int) : Status(amount) {
    override val strId = "base:status:Overloaded"
    override val displayAmount = true

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        amount--
        api.notifyStatusUpdated()
    }
}
