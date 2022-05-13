package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class Splash : StandardSpell() {
    override val id: String = "base:spells:Splash"
    override val requirement: ComponentRequirement = Single(SuitGroup.ICE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val glowType: GlowType = GlowType.ICE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        LimitedRepeatableAspect(2),
        FluxGainAspect(1),
        AttackDamageAspect(0),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, Element.ICE, baseDamage(), flags())
    }
}
