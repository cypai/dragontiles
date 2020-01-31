package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.combat.StatusData
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class ElementalRune(upgraded: Boolean) : Rune(upgraded) {
    override val id: String = "base:spells:ElementalRune"

    override val requirement: ComponentRequirement = IdenticalX(SuitGroup.ELEMENTAL)

    override fun attackDamageModifier(damageOrigin: DamageOrigin, damageTarget: DamageTarget, attackerStatus: StatusData, targetStatus: StatusData, element: Element, amount: Int): Int {
        val c = components()
        return if (active && damageOrigin == DamageOrigin.HERO_ATTACK && element == elemental(c)) {
            c.size
        } else {
            0
        }
    }

    override fun newClone(upgraded: Boolean): ElementalRune {
        return ElementalRune(upgraded)
    }
}
