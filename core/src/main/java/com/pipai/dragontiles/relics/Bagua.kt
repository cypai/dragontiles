package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity

class Bagua : Relic() {
    override val id = "base:relics:Bagua"
    override val assetName = "bagua.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        api.aoeAttack(Element.NONE, 2 * api.combat.assigned.values.flatten().size, listOf())
    }
}
