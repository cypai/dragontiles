package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Enpowered
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.utils.getStackableAmount

class EnpowerElement : StandardSpell() {
    override val id: String = "base:spells:EnpowerElement"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(3, "base:status:Enpowered"), 1),
        FluxGainAspect(2),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val amount = aspects.getStackableAmount(GenericStatus::class)
        api.addStatusToHero(Enpowered(amount, elemental(components())))
    }
}
