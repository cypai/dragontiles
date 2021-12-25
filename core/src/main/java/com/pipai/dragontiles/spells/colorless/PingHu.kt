package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.*

class PingHu : Sorcery() {
    override val id = "base:sorceries:PingHu"
    override val rarity = Rarity.UNCOMMON
    override val requirement = object : CustomRequirement() {
        override val type: SetType = SetType.SEQUENTIAL
        override var suitGroup: SuitGroup = SuitGroup.ELEMENTAL
        override val description: String = "All melds are sequences."

        override fun satisfied(slots: List<TileInstance>): Boolean {
            throw NotImplementedError()
        }

        override fun satisfied(fullCastHand: FullCastHand): Boolean {
            return fullCastHand.melds.all { it.type == MeldType.SEQUENCE }
        }
    }
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(15)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(Element.NONE, baseDamage(), flags())
    }
}
