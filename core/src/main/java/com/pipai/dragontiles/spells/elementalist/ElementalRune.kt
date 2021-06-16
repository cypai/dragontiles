package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class ElementalRune(upgraded: Boolean) : Rune(upgraded) {
    override val id: String = "base:spells:ElementalRune"
    override val rarity: Rarity = Rarity.COMMON

    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ELEMENTAL)

    override fun newClone(upgraded: Boolean): ElementalRune {
        return ElementalRune(upgraded)
    }

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int {
        return if (active && origin == DamageOrigin.SELF_ATTACK && target == DamageTarget.OPPONENT && element.isElemental) {
            2
        } else {
            0
        }
    }
}
