package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Megaxplosion : StandardSpell() {
    override val id: String = "base:spells:Megaxplosion"
    override val requirement: ComponentRequirement = Identical(6, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(88),
        FluxGainAspect(8),
    )
    override val scoreable: Boolean = true

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
