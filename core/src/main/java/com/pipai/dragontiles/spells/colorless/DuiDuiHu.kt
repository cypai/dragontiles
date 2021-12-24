package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.*

class DuiDuiHu : Sorcery() {
    override val id = "base:sorceries:DuiDuiHu"
    override val rarity = Rarity.UNCOMMON
    override val requirement = object : CustomRequirement() {
        override val type: SetType = SetType.IDENTICAL
        override var suitGroup: SuitGroup = SuitGroup.ANY_NO_FUMBLE
        override val description: String = "All melds are identical."

        override fun satisfied(slots: List<TileInstance>): Boolean {
            throw NotImplementedError()
        }

        override fun satisfied(fullCastHand: FullCastHand): Boolean {
            return fullCastHand.melds.all { it.type == MeldType.IDENTICAL }
        }
    }
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(20)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(Element.NONE, baseDamage(), flags(), asAttack = false)
    }
}