package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class ElementX : Rune() {
    override val id: String = "base:spells:ElementX"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX(SuitGroup.ELEMENTAL)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        XAspect(2, 0),
    )

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int {
        val c = components()
        return if (active
            && origin == Combatant.HeroCombatant
            && element == elemental(c)
        ) {
            x()
        } else {
            0
        }
    }
}
