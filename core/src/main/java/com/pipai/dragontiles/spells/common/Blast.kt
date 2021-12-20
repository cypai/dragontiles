package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Blast : StandardSpell() {
    override val id: String = "base:spells:Blast"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(8),
        FluxGainAspect(3)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        params.targets.forEach {
            api.attack(api.getEnemy(it), elemental(components()), baseDamage())
        }
    }
}
