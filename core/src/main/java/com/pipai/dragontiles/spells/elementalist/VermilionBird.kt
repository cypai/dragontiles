package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.with

class VermilionBird : Sorcery() {
    override val id: String = "base:spells:VermilionBird"
    override val requirement: ComponentRequirement = Sequential(9)
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(81),
    )

    override val scoreable: Boolean = true

    override fun flags(): List<CombatFlag> {
        return super.flags().with(CombatFlag.PIERCING)
    }

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(Element.FIRE, baseDamage(), flags())
    }
}
