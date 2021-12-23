package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Strength
import kotlinx.serialization.Serializable

class Bamboo : Relic() {
    override val id = "base:relics:Bamboo"
    override val assetName = "bamboo.png"
    override val rarity = Rarity.COMMON

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.addStatusToHero(Strength(1))
        }
    }
}
