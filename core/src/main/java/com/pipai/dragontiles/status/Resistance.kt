package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class Resistance(
    amount: Int,
    private val element: Element,
    private val decreasing: Boolean,
    private var skip: Boolean = false,
) :
    SimpleStatus(
        when (element) {
            Element.FIRE -> "base:status:FireResistance"
            Element.ICE -> "base:status:IceResistance"
            Element.LIGHTNING -> "base:status:LightningResistance"
            Element.NONE -> "base:status:NonElementalResistance"
        },
        when (element) {
            Element.FIRE -> "fire_resistance.png"
            Element.ICE -> "ice_resistance.png"
            Element.LIGHTNING -> "lightning_resistance.png"
            Element.NONE -> "nonelemental_resistance.png"
        },
        decreasing,
        amount,
    ) {

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float {
        return if (target == combatant && element == this.element) {
            0f
        } else {
            1f
        }
    }

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int {
        return if (target == combatant && element == this.element) {
            1
        } else {
            0
        }
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        if (decreasing) {
            if (skip) {
                skip = false
            } else {
                amount--
            }
        }
        api.notifyStatusUpdated()
    }
}
