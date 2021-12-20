package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class Weak(amount: Int, var skip: Boolean) : Status(amount) {
    override val strId = "base:status:Weak"
    override val assetName = "assets/binassets/graphics/status/weak.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Weak(amount, skip)
    }

    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float {
        return if (origin == DamageOrigin.SELF_ATTACK && target == DamageTarget.OPPONENT) {
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
