package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.*

class Explosion : StandardSpell() {
    override val id: String = "base:spells:Explosion"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(0),
        FluxGainAspect(5),
        PostExhaustAspect(),
    )

    override fun dynamicBaseDamage(components: List<TileInstance>): Int {
        return 3 * numeric(components)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        exhausted = true
        api.aoeAttack(elemental(components()), baseDamage() + dynamicBaseDamage(components()), flags())
    }
}
