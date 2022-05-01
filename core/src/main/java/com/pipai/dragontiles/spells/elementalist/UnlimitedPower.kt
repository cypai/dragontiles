package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Enpowered

class UnlimitedPower : StandardSpell() {
    override val id: String = "base:spells:UnlimitedPower"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.LIFE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(6),
        ExhaustAspect(),
    )

    override fun additionalLocalized(): List<String> {
        return listOf("base:status:Enpowered")
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        var amount = api.heroStatusAmount("base:status:EnpoweredFire")
        api.addStatusToHero(Enpowered(amount, Element.FIRE))
        amount = api.heroStatusAmount("base:status:EnpoweredIce")
        api.addStatusToHero(Enpowered(amount, Element.ICE))
        amount = api.heroStatusAmount("base:status:EnpoweredLightning")
        api.addStatusToHero(Enpowered(amount, Element.LIGHTNING))
        amount = api.heroStatusAmount("base:status:EnpoweredNonElemental")
        api.addStatusToHero(Enpowered(amount, Element.NONE))
    }
}
