package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.SpellType

class Shuriken : Relic() {
    override val id = "base:relics:Shuriken"
    override val assetName = "shuriken.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = true

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        counter = 0
    }

    @CombatSubscribe
    suspend fun onCast(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.ATTACK) {
            counter ++
            if (counter >= 3) {
                api.aoeAttack(Element.NONE, 5, listOf())
            }
        }
    }
}
