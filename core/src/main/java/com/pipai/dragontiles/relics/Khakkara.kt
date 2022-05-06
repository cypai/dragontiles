package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Vulnerable
import com.pipai.dragontiles.status.Weak

class Khakkara : Relic() {
    override val id = "base:relics:Khakkara"
    override val assetName = "khakkara.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    override fun additionalLocalized(): List<String> {
        return listOf("base:status:Weak")
    }

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.addAoeStatus(Weak(1, false))
        }
    }
}
