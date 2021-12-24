package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class ElementalRune : Rune() {
    override val id: String = "base:spells:ElementalRune"
    override val rarity: Rarity = Rarity.STARTER
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ELEMENTAL)
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override fun queryFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int {
        val c = components()
        return if (active
            && origin == Combatant.HeroCombatant
            && element == elemental(c)
        ) {
            2
        } else {
            0
        }
    }
}
