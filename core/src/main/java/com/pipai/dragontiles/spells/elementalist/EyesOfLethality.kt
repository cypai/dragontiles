package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class EyesOfLethality : Sorcery() {
    override val id = "base:sorceries:EyesOfLethality"
    override val requirement = Identical(2)
    override val rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2)
    )
    override val scoreable: Boolean = true

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}