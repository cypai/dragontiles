package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Enpowered
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.utils.getStackableAmount

class Enpowerment : PowerSpell() {
    override val id: String = "base:spells:Enpowerment"
    override val requirement: ComponentRequirement = RainbowIdentical(2)
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(2, "base:status:Enpower"), 1),
        FluxGainAspect(2),
    )
    override val scoreable: Boolean = true

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(EnpowermentStatus(aspects.getStackableAmount(GenericStatus::class)))
    }

    class EnpowermentStatus(amount: Int) : SimpleStatus("base:status:Enpowerment", "enpowerment.png", true, amount) {
        @CombatSubscribe
        suspend fun onPlayerTurnEnd(ev: TurnEndEvent, api: CombatApi) {
            api.addStatusToHero(Enpowered(amount, Element.FIRE))
            api.addStatusToHero(Enpowered(amount, Element.ICE))
            api.addStatusToHero(Enpowered(amount, Element.LIGHTNING))
        }
    }
}
