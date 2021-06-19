package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class BreakStatus(amount: Int, var skip: Boolean) : Status(amount) {
    override val strId = "base:status:Break"
    override val assetName = "assets/binassets/graphics/status/break.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return BreakStatus(amount, skip)
    }

    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float {
        return if (origin == DamageOrigin.OPPONENT_ATTACK) {
            1.5f
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
