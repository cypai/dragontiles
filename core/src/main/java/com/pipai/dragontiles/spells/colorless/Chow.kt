package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Chow : Sorcery() {
    override val id = "base:spells:Chow"
    override val requirement = Sequential(3)
    override val rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(5)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
