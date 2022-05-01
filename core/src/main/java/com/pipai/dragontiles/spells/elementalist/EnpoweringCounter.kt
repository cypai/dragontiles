package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.EnpoweredStatus
import com.pipai.dragontiles.status.SimpleStatus

class EnpoweringCounter : StandardSpell() {
    override val id: String = "base:spells:EnpoweringCounter"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(5),
    )

    override fun additionalLocalized(): List<String> {
        return listOf("base:status:Enpower")
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(EnpoweringCounterStatus(1, elemental(components())))
    }

    class EnpoweringCounterStatus(
        amount: Int,
        private val element: Element,
    ) :
        SimpleStatus(
            when (element) {
                Element.FIRE -> "base:status:FireEnpoweringCounter"
                Element.ICE -> "base:status:IceEnpoweringCounter"
                Element.LIGHTNING -> "base:status:LightningEnpoweringCounter"
                Element.NONE -> "base:status:NonElementalEnpoweringCounter"
            },
            when (element) {
                Element.FIRE -> "red.png"
                Element.ICE -> "blue.png"
                Element.LIGHTNING -> "yellow.png"
                Element.NONE -> "gray.png"
            },
            true,
            amount,
        ) {

        @CombatSubscribe
        suspend fun onAttackedFlux(ev: PlayerFluxDamageEvent, api: CombatApi) {
            if (CombatFlag.ATTACK in ev.flags) {
                api.addStatusToHero(EnpoweredStatus(amount, element))
            }
        }

        @CombatSubscribe
        suspend fun onAttackedDamage(ev: PlayerDamageEvent, api: CombatApi) {
            if (CombatFlag.ATTACK in ev.flags) {
                api.addStatusToHero(EnpoweredStatus(amount, element))
            }
        }

        @CombatSubscribe
        suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
            api.removeHeroStatus(id)
        }
    }
}
