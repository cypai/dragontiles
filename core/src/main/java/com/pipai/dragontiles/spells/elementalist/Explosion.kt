package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.*

class Explosion : StandardSpell() {
    override val id: String = "base:spells:Explosion"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val glowType: GlowType = GlowType.ELEMENTED
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(0),
        FluxGainAspect(8),
        ExhaustAspect(),
    )

    override fun dynamicBaseDamage(components: List<TileInstance>, api: CombatApi): Int {
        return 3 * numeric(components)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage() + dynamicBaseDamage(components(), api), flags())
    }
}
