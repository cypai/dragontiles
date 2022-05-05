package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.RuneDeactivatedEvent
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity

class LotusFlower : Relic() {
    override val id = "base:relics:LotusFlower"
    override val assetName = "lotus_flower.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onDeactivate(ev: RuneDeactivatedEvent, api: CombatApi) {
        api.heroLoseFlux(ev.components.size * 2)
    }
}
