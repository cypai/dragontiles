package com.pipai.dragontiles.sorceries

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.*

class CommonSorcery : Sorcery() {
    override val strId = "base:sorceries:CommonSorcery"
    override val requirement = object : CustomRequirement() {
        override val description: String = "All melds are sequences."

        override fun satisfied(slots: List<TileInstance>): Boolean {
            throw NotImplementedError()
        }

        override fun satisfied(fullCastHand: FullCastHand): Boolean {
            return fullCastHand.melds.all { it.type == MeldType.IDENTICAL }
        }
    }
    override val rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(10)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(Element.NONE, baseDamage(), false)
    }
}
