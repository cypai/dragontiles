package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.spells.Rarity

class Cong : Relic() {
    override val id = "base:relics:Cong"
    override val assetName = "cong.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        if (ev.turnNumber == 2) {
            api.heroLoseFlux(10)
        }
    }
}
