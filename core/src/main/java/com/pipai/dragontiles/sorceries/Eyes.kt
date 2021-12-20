package com.pipai.dragontiles.sorceries

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Eyes : Sorcery() {
    override val strId = "base:sorceries:Eyes"
    override val requirement = Identical(2)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(4)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), false)
    }
}
