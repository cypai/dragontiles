package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.with

class Faan : Sorcery() {
    override val id = "base:spells:Faan"
    override val requirement = Identical(2)
    override val rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(10)
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().with(CombatFlag.PIERCING)
    }

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
