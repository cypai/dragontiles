package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.spells.*

class FluxBlast : StandardSpell() {
    override val id: String = "base:spells:FluxBlast"
    override val requirement: ComponentRequirement = Identical(4)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(0),
        FluxGainAspect(40),
    )
    override val scoreable: Boolean = true

    override fun dynamicBaseDamage(components: List<TileInstance>, api: CombatApi): Int {
        val hero = api.runData.hero
        return hero.fluxMax + hero.tempFluxMax - hero.flux
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage() + dynamicBaseDamage(components(), api), flags())
    }
}
