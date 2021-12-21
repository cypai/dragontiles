package com.pipai.dragontiles.sorceries

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Chow : Sorcery() {
    override val strId = "base:sorceries:Chow"
    override val requirement = Sequential(3)
    override val rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(3)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), false)
    }
}
