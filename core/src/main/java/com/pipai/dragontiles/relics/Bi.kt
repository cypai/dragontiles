package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.spells.Rarity

class Bi : Relic() {
    override val id = "base:relics:Bi"
    override val assetName = "bi.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.heroLoseFlux(7)
        }
    }
}
