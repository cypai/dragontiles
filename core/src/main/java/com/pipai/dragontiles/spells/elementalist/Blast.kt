package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Blast : StandardSpell() {
    override val id: String = "base:spells:Blast"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.COMMON
    override val glowType: GlowType = GlowType.ELEMENTED
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(15),
        FluxGainAspect(7)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
