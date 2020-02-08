package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.combat.StatusData
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class StrengthRune(upgraded: Boolean) : Rune(upgraded) {
    override val id: String = "base:spells:StrengthRune"
    override val rarity: Rarity = Rarity.UNCOMMON

    override val requirement: ComponentRequirement = IdenticalX()

    override fun attackDamageModifier(damageOrigin: DamageOrigin, damageTarget: DamageTarget, attackerStatus: StatusData, targetStatus: StatusData, element: Element, amount: Int): Int {
        return if (active && damageOrigin == DamageOrigin.HERO_ATTACK) {
            components().size + if (upgraded) 1 else 0
        } else {
            0
        }
    }

    override fun newClone(upgraded: Boolean): StrengthRune {
        return StrengthRune(upgraded)
    }
}
