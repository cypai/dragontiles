package com.pipai.dragontiles.sorceries

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Pong : Sorcery() {
    override val strId = "base:sorceries:Pong"
    override val requirement = Identical(3)
    override val rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(10)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), false)
    }
}
