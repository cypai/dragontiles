package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.utils.findAsWhere

class BreakingBlast : StandardSpell() {
    override val id: String = "base:spells:BreakingBlast"
    override val requirement: ComponentRequirement = Identical(2)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(4),
        StackableAspect(BreakStatus(1, false), 1),
        FluxGainAspect(5),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
        val stackable = aspects.findAsWhere(StackableAspect::class) { it.status is BreakStatus }!!
        api.addAoeStatus(stackable.status.deepCopy())
    }
}
