package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*

class Regen(amount: Int, private val decreasing: Boolean) : Status(amount) {
    override val id = "base:status:Regen"
    override val assetName = "regen.png"
    override val displayAmount = true
    override val negativeAllowed = false
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Regen(amount, decreasing)
    }

    @CombatSubscribe
    suspend fun onPlayerTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        if (combatant == Combatant.HeroCombatant) {
            api.healHero(amount)
            if (decreasing) {
                amount--
                api.notifyStatusUpdated()
            }
        }
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        val c = combatant
        if (c is Combatant.EnemyCombatant) {
            api.healEnemy(c.enemy, amount)
            if (decreasing) {
                amount--
                api.notifyStatusUpdated()
            }
        }
    }
}
