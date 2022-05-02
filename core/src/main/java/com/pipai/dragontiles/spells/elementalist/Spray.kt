package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.choose

class Spray : StandardSpell() {
    override val id: String = "base:spells:Spray"
    override val requirement: ComponentRequirement = Sequential(2)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2),
        FluxGainAspect(6),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        repeat(3) {
            val target = api.getLiveEnemies().choose(api.runData.seed.miscRng())
            api.attack(target, elemental(components()), baseDamage(), flags())
        }
    }
}
