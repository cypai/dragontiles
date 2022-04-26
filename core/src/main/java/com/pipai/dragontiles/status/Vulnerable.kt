package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class Vulnerable(amount: Int, var skip: Boolean) : Status(amount) {
    override val id = "base:status:Vulnerable"
    override val assetName = "vulnerable.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = true

    override fun deepCopy(): Status {
        return Vulnerable(amount, skip)
    }

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float {
        return if (flags.contains(CombatFlag.ATTACK) && target == combatant) {
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
