package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.spells.Rarity

class Huang : Relic() {
    override val id = "base:relics:Huang"
    override val assetName = "huang.png"
    override val rarity = Rarity.RARE
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        if (ev.turnNumber == 3) {
            api.heroLoseFlux(13)
        }
    }
}
