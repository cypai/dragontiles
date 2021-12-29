package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity

class PerfectFreeze : Relic() {
    override val id = "base:relics:PerfectFreeze"
    override val assetName = "perfect_freeze.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int {
        return if (origin is Combatant.HeroCombatant && element == Element.ICE && flags.any { it == CombatFlag.ATTACK }) {
            3
        } else {
            0
        }
    }
}
