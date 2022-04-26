package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent

class NoDraw(amount: Int) : Status(amount) {
    override val id = "base:status:NoDraw"
    override val assetName = "nodraw.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = true

    override fun deepCopy(): Status {
        return NoDraw(amount)
    }

    @CombatSubscribe
    suspend fun onPlayerTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        amount--
        api.notifyStatusUpdated()
    }
}
