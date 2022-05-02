package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withoutAll

class FlareBlitz : StandardSpell() {
    override val id: String = "base:spells:FlareBlitz"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(12),
        FluxGainAspect(8),
    )

    override fun dynamicBaseDamage(components: List<TileInstance>, api: CombatApi): Int {
        val burns = api.combat.hand
            .withoutAll(components)
            .filter { it.tileStatus == TileStatus.BURN }
            .size
        return baseDamage() + (8 * burns)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
    }
}
