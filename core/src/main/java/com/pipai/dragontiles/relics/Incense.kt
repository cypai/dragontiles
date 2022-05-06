package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Immunized
import com.pipai.dragontiles.status.Strength
import kotlinx.serialization.Serializable

class Incense : Relic() {
    override val id = "base:relics:Incense"
    override val assetName = "incense.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    override fun additionalLocalized(): List<String> {
        return listOf("base:status:Immunized")
    }

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.addStatusToHero(Immunized(1))
        }
    }
}
