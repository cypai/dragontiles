package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class EnpoweredStatus(amount: Int, private val enpoweredElement: Element) :
    SimpleStatus(
        when (enpoweredElement) {
            Element.FIRE -> "base:status:EnpoweredFire"
            Element.ICE -> "base:status:EnpoweredIce"
            Element.LIGHTNING -> "base:status:EnpoweredLightning"
            Element.NONE -> "base:status:EnpoweredNonElemental"
        },
        when (enpoweredElement) {
            Element.FIRE -> "red.png"
            Element.ICE -> "blue.png"
            Element.LIGHTNING -> "yellow.png"
            Element.NONE -> "gray.png"
        },
        true,
        amount,
    ) {

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int {
        return if (origin == Combatant.HeroCombatant && element == enpoweredElement && flags.any { it == CombatFlag.ATTACK || it == CombatFlag.SORCERY }) {
            amount
        } else {
            0
        }
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        amount--
        api.notifyStatusUpdated()
    }
}
