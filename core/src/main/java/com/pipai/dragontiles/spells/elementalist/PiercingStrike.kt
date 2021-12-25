package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withAll

class PiercingStrike : StandardSpell() {
    override val id: String = "base:spells:PiercingStrike"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(3),
        FluxGainAspect(3),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.PIERCING))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags = flags())
    }
}
