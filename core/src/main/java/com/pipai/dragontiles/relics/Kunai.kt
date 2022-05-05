package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.SpellType

class Kunai : Relic() {
    override val id = "base:relics:Kunai"
    override val assetName = "kunai.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = true

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        counter = 0
    }

    @CombatSubscribe
    suspend fun onCast(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.EFFECT) {
            counter ++
            if (counter >= 5) {
                api.aoeAttack(Element.NONE, 5, listOf())
            }
        }
    }
}
