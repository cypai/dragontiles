package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.SpellType

class Nunchaku : Relic() {
    override val id = "base:relics:Nunchaku"
    override val assetName = "nunchaku.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = true

    @CombatSubscribe
    suspend fun onAttack(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.ATTACK) {
            counter++
            api.updateRelicCounter(this)
            if (counter >= 5) {
                counter = 0
                api.updateRelicCounter(this)
                api.drawToPool(1)
            }
        }
    }
}
