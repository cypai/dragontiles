package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*

class Discharge : StandardSpell() {
    override val id: String = "base:spells:Discharge"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        val amount = api.runData.hero.flux / 2
        api.heroLoseFlux(amount)
        api.attack(target, Element.LIGHTNING, baseDamage(), flags())
    }
}
