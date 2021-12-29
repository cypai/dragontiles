package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.ComponentConsumeEvent
import com.pipai.dragontiles.spells.Rarity

class Inkstone : Relic() {
    override val id = "base:relics:Inkstone"
    override val assetName = "inkstone.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = true

    @CombatSubscribe
    suspend fun onConsume(ev: ComponentConsumeEvent, api: CombatApi) {
        counter += ev.components.size
        if (counter >= 14) {
            api.swapQuery(1)
            counter -= 14
        }
    }
}
