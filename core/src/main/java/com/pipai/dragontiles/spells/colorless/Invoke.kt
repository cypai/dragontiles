package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*

class Invoke : StandardSpell() {
    override val id: String = "base:spells:Invoke"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2), FluxGainAspect(1)
    )

    override fun flags(): List<CombatFlag> {
        return listOf(CombatFlag.INVOKE)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
    }
}
