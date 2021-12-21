package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class ElementalRune : Rune() {
    override val strId: String = "base:spells:ElementalRune"
    override val rarity: Rarity = Rarity.COMMON
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ELEMENTAL)
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int {
        val c = components()
        return if (active
            && origin == DamageOrigin.SELF_ATTACK
            && target == DamageTarget.OPPONENT
            && element == elemental(c)
        ) {
            2
        } else {
            0
        }
    }
}
