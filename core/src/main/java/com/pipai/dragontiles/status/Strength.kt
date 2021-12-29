package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element

class Strength(amount: Int) : Status(amount) {
    override val id = "base:status:Strength"
    override val assetName = "strength.png"
    override val displayAmount = true
    override val isDebuff: Boolean = false

    override fun deepCopy(): Status {
        return Strength(amount)
    }

    override fun queryFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int {
        return if (origin == combatant) {
            amount
        } else {
            0
        }
    }
}
