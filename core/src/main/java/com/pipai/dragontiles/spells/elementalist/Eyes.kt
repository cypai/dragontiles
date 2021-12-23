package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Eyes : Sorcery() {
    override val strId = "base:sorceries:Eyes"
    override val requirement = Identical(2)
    override val rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), false)
    }
}
