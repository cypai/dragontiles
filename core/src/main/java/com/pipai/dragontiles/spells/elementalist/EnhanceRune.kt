package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class EnhanceRune(upgraded: Boolean) : Rune(upgraded) {
    override val id: String = "base:spells:EnhanceRune"
    override val rarity: Rarity = Rarity.UNCOMMON

    override val requirement: ComponentRequirement = SequentialX()

    override fun newClone(upgraded: Boolean): EnhanceRune {
        return EnhanceRune(upgraded)
    }

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
