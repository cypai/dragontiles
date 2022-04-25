package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.ComponentConsumeEvent
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.spells.Rarity

class Inkstone : Relic() {
    override val id = "base:relics:Inkstone"
    override val assetName = "inkstone.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = true

    private var swap = false

    @CombatSubscribe
    suspend fun onConsume(ev: ComponentConsumeEvent, api: CombatApi) {
        counter += ev.components.size
        api.updateRelicCounter(this)
        if (counter >= 14) {
            counter -= 14
            api.updateRelicCounter(this)
            swap = true
        }
    }

    @CombatSubscribe
    suspend fun onCast(ev: SpellCastedEvent, api: CombatApi) {
        if (swap) {
            api.swapQuery(1)
            swap = false
        }
    }
}
