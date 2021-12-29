package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class Weak(amount: Int, var skip: Boolean) : Status(amount) {
    override val id = "base:status:Weak"
    override val assetName = "weak.png"
    override val displayAmount = true
    override val isDebuff: Boolean = true

    override fun deepCopy(): Status {
        return Weak(amount, skip)
    }

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float {
        return if (origin == combatant) {
            0.7f
        } else {
            1f
        }
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        if (skip) {
            skip = false
        } else {
            amount--
            api.notifyStatusUpdated()
        }
    }
}
