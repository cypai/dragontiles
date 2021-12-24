package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Strike : StandardSpell() {
    override val id: String = "base:spells:Strike"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(7),
        FluxGainAspect(2)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }
}
