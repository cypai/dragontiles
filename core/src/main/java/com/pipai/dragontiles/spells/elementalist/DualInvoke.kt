package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class DualInvoke : StandardSpell() {
    override val strId: String = "base:spells:DualInvoke"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(1),
        FluxGainAspect(2)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        repeat(2) {
            api.attack(target, elemental(components()), baseDamage())
        }
    }
}
