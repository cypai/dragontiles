package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.Rarity

class FireFlower : Relic() {
    override val id = "base:relics:FireFlower"
    override val assetName = "fire_flower.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int {
        return if (origin is Combatant.HeroCombatant && element == Element.FIRE && flags.any { it == CombatFlag.ATTACK }) {
            3
        } else {
            0
        }
    }
}
