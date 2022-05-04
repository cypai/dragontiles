package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Enpowered
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.utils.getStackableAmount

class Masochism : StandardSpell() {
    override val id: String = "base:spells:Masochism"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(2), 1),
        FluxGainAspect(2),
    )

    override fun additionalLocalized(): List<String> {
        return listOf("base:status:Enpowered")
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val amount = aspects.getStackableAmount(GenericStatus::class)
        api.addStatusToHero(MasochismStatus(amount, elemental(components())))
    }

    class MasochismStatus(
        amount: Int,
        private val element: Element,
    ) :
        SimpleStatus(
            when (element) {
                Element.FIRE -> "base:status:FireMasochism"
                Element.ICE -> "base:status:IceMasochism"
                Element.LIGHTNING -> "base:status:LightningMasochism"
                Element.NONE -> "base:status:NonElementalMasochism"
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
        suspend fun onHit(ev: EnemyHitPlayerEvent, api: CombatApi) {
            if (CombatFlag.ATTACK in ev.flags) {
                api.addStatusToHero(Enpowered(amount, element))
            }
        }

        @CombatSubscribe
        suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
            api.removeHeroStatus(id)
        }
    }
}
