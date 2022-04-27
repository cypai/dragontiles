package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class Cyclone : StandardSpell() {
    override val id: String = "base:spells:Cyclone"
    override val requirement: ComponentRequirement = IdenticalX()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(5),
        FluxGainAspect(8),
        XAspect(0),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        repeat(x()) {
            api.aoeAttack(elemental(components()), baseDamage(), flags())
        }
    }
}
