package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Expansion : Sorcery() {
    override val id: String = "base:spells:Expansion"
    override val requirement: ComponentRequirement = AnyCombo(3)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        TempMaxFluxChangeAspect(5)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
    }
}
