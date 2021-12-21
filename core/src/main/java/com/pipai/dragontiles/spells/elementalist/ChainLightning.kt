package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class ChainLightning : StandardSpell() {
    override val strId: String = "base:spells:ChainLightning"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(14),
        FluxGainAspect(4),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
        api.inflictTileStatusOnHand(RandomTileStatusInflictStrategy(TileStatus.SHOCK, 1))
    }
}
