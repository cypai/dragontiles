package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Dodge

class RabbitsFoot : Relic() {
    override val id = "base:relics:RabbitsFoot"
    override val assetName = "rabbit_foot.png"
    override val rarity = Rarity.SPECIAL
    override val showCounter: Boolean = false
    override fun additionalLocalized(): List<String> = listOf(Dodge(0).id)

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.addStatusToHero(Dodge(1))
        }
    }
}
