package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.ComponentConsumeEvent
import com.pipai.dragontiles.spells.Rarity

class SomersaultCloud : Relic() {
    override val id = "base:relics:SomersaultCloud"
    override val assetName = "somersault_cloud.png"
    override val rarity = Rarity.RARE
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onConsume(ev: ComponentConsumeEvent, api: CombatApi) {
        if (api.getHandTiles().isEmpty()) {
            api.draw(api.runData.hero.handSize)
        }
    }
}
