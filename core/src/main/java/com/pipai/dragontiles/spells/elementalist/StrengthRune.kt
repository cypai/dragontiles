package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class StrengthRune : Rune() {
    override val strId: String = "base:spells:StrengthRune"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX()
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int {
        return if (active && origin == DamageOrigin.SELF_ATTACK && target == DamageTarget.OPPONENT) {
            components().size
        } else {
            0
        }
    }
}
