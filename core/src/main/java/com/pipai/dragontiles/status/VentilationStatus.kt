package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.TurnStartEvent

class VentilationStatus(amount: Int) : Status(amount) {
    override val id = "base:status:Ventilation"
    override val assetName = "ventilation.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return VentilationStatus(amount)
    }

    @CombatSubscribe
    suspend fun onPlayerStartTurn(ev: TurnStartEvent, api: CombatApi) {
        when (val c = combatant) {
            is Combatant.HeroCombatant -> {
                if (!api.heroHasStatus(Overloaded::class)) {
                    api.heroLoseFlux(amount)
                }
            }
            is Combatant.EnemyCombatant -> {
                if (!api.enemyHasStatus(c.enemy, Overloaded::class)) {
                    api.enemyLoseFlux(c.enemy, amount)
                }
            }
            else -> {
            }
        }
    }
}