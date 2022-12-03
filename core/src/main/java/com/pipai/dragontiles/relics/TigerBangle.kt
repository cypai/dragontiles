package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.Rarity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class TigerBangle : Relic() {
    override val id = "base:relics:TigerBangle"
    override val assetName = "transmuter.png"
    override val rarity = Rarity.STARTER
    override val showCounter: Boolean = true

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            counter = 2
            api.updateRelicCounter(this)
        }
    }

    override suspend fun onTrigger(api: CombatApi) {
        if (counter > 0) {
            counter--
            api.updateRelicCounter(this)
            api.queryPoolDraw(1)
        }
    }
}
