package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.utils.findAs

class Enpower : StandardSpell() {
    override val id: String = "base:spells:Enpower"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(2),
    )

    private val amount = 6

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val type = when (val element = elemental(components())) {
            Element.FIRE -> EnpowerStatus(amount, element, "base:status:EnpoweredFire", "red.png")
            Element.ICE -> EnpowerStatus(amount, element, "base:status:EnpoweredIce", "blue.png")
            Element.LIGHTNING -> EnpowerStatus(amount, element, "base:status:EnpoweredLightning", "yellow.png")
            else -> {
                throw IllegalStateException("Unexpected element")
            }
        }
        api.addStatusToHero(type)
    }

    class EnpowerStatus(amount: Int, private val enpoweredElement: Element, id: String, assetName: String) :
        SimpleStatus(id, assetName, true, amount) {

        override fun queryFlatAdjustment(
            origin: Combatant?,
            target: Combatant?,
            element: Element,
            flags: List<CombatFlag>
        ): Int {
            return if (origin == Combatant.HeroCombatant && element == enpoweredElement && CombatFlag.ATTACK in flags) {
                amount
            } else {
                0
            }
        }

        @CombatSubscribe
        suspend fun onAttack(ev: PlayerAttackEnemyEvent, api: CombatApi) {
            if (ev.element == enpoweredElement) {
                api.removeHeroStatus(id)
            }
        }
    }
}