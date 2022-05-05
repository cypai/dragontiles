package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Vulnerable

class Firecracker : Relic() {
    override val id = "base:relics:Firecracker"
    override val assetName = "firecracker.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.addAoeStatus(Vulnerable(1, false))
        }
    }
}
