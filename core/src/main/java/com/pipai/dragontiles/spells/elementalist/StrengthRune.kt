package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.ComponentRequirement
import com.pipai.dragontiles.spells.IdenticalX
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Rune

class StrengthRune(upgraded: Boolean) : Rune(upgraded) {
    override val id: String = "base:spells:StrengthRune"
    override val rarity: Rarity = Rarity.UNCOMMON

    override val requirement: ComponentRequirement = IdenticalX()

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int {
        return if (active && origin == DamageOrigin.SELF_ATTACK && target == DamageTarget.OPPONENT) {
            components().size
        } else {
            0
        }
    }

    override fun newClone(upgraded: Boolean): StrengthRune {
        return StrengthRune(upgraded)
    }
}
