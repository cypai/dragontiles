package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class RapidMiniInvoke : StandardSpell() {
    override val id: String = "base:spells:RapidMiniInvoke"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(1),
        FluxGainAspect(5),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        repeat(5) {
            api.attack(target, elemental(components()), baseDamage(), flags())
        }
    }
}
