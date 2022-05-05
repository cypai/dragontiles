package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity

class LightningRod : Relic() {
    override val id = "base:relics:LightningRod"
    override val assetName = "lightningrod.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int {
        return if (origin is Combatant.HeroCombatant && element == Element.LIGHTNING && flags.any { it == CombatFlag.ATTACK }) {
            2
        } else {
            0
        }
    }
}
