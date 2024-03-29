package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Eyes : Sorcery() {
    override val id = "base:spells:Eyes"
    override val requirement = Identical(2)
    override val rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(10)
    )
    override val scoreable: Boolean = true

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
