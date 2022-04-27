package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*

class VentilationStatus(amount: Int) : Status(amount) {
    override val id = "base:status:Ventilation"
    override val assetName = "ventilation.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return VentilationStatus(amount)
    }

    @CombatSubscribe
    suspend fun onPlayerEndTurn(ev: TurnEndEvent, api: CombatApi) {
        if (combatant is Combatant.HeroCombatant) {
            api.heroLoseFlux(amount)
        }
    }

    @CombatSubscribe
    suspend fun onEnemyEndTurn(ev: EnemyTurnEndEvent, api: CombatApi) {
        val c = combatant
        if (c is Combatant.EnemyCombatant) {
            api.enemyLoseFlux(c.enemy, amount)
        }
    }
}
