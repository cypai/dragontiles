package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.SpellType

class IronRake : Relic() {
    override val id = "base:relics:IronRake"
    override val assetName = "iron_rake.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = true

    @CombatSubscribe
    fun onCast(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.ATTACK) {
            counter++
            if (counter > 8) {
                counter = 0
            }
        }
    }

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float {
        return if (counter >= 8 && origin == Combatant.HeroCombatant && CombatFlag.ATTACK in flags) {
            2f
        } else {
            1f
        }
    }
}
