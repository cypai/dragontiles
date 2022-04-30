package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.VentilationStatus
import com.pipai.dragontiles.utils.findAs

class Ventilation : PowerSpell() {
    override val id: String = "base:spells:Ventilation"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ARCANE)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(VentilationStatus(5), 1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val amount = aspects.findAs(StackableAspect::class)!!.status.amount
        api.addStatusToHero(VentilationStatus(amount))
    }
}
