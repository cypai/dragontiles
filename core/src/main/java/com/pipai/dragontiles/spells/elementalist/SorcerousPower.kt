package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.EnpoweredStatus
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.utils.getStackableAmount

class SorcerousPower : Sorcery() {
    override val id: String = "base:sorceries:SorcerousPower"
    override val requirement: ComponentRequirement = AnyCombo(3)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(3, "base:status:Enpower"), 1),
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        val amount = aspects.getStackableAmount(GenericStatus::class)
        api.addStatusToHero(EnpoweredStatus(amount, elemental(components())))
    }
}