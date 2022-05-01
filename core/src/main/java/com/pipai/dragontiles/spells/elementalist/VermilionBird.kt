package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus

class VermilionBird : Sorcery() {
    override val id: String = "base:spells:VermilionBird"
    override val requirement: ComponentRequirement = Sequential(9)
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override val scoreable: Boolean = true

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.addStatusToHero(VermilionBirdStatus(1))
    }

    class VermilionBirdStatus(amount: Int) : SimpleStatus("base:status:VermilionBird", "red.png", true, amount) {
        override fun queryScaledAdjustment(
            origin: Combatant?,
            target: Combatant?,
            element: Element,
            flags: List<CombatFlag>
        ): Float {
            return if (origin == Combatant.HeroCombatant && element == Element.FIRE) {
                2f
            } else {
                1f
            }
        }
    }
}
