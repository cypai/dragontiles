package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.Rarity

class IChing : Relic() {
    override val id = "base:relics:IChing"
    override val assetName = "i_ching.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    private var played = false

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        played = false
    }

    @CombatSubscribe
    suspend fun onCast(ev: SpellCastedEvent, api: CombatApi) {
        if (!played) {
            api.queryPoolDraw(1)
            played = true
        }
    }
}
