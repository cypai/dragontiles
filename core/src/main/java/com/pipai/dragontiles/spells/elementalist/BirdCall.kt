package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class BirdCall : StandardSpell() {
    override val id: String = "base:spells:BirdCall"
    override val type: SpellType = SpellType.ATTACK
    override val rarity: Rarity = Rarity.RARE
    override val targetType: TargetType = TargetType.AOE
    override val requirement: ComponentRequirement = Sequential(9)
    override val glowType: GlowType = GlowType.FIRE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(9),
        AttackDamageAspect(81),
    )
    override val scoreable: Boolean = true

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(Element.FIRE, baseDamage(), flags())
    }
}
