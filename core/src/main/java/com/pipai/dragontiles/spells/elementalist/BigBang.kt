package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class BigBang : Sorcery() {
    override val id = "base:spells:BigBang"
    override val requirement = AnyCombo(3)
    override val rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(6)
    )

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
