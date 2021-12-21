package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Dodge

class RabbitsFoot : Relic() {
    override val strId = "base:relics:RabbitsFoot"
    override val assetName = "rabbit_foot.png"
    override val rarity = Rarity.COMMON

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.addStatusToHero(Dodge(1))
        }
    }
}
