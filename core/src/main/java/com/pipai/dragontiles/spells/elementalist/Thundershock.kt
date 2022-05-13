package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class Thundershock : StandardSpell() {
    override val id: String = "base:spells:Thundershock"
    override val requirement: ComponentRequirement = Sequential(2, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.COMMON
    override val glowType: GlowType = GlowType.LIGHTNING
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(7),
        FluxGainAspect(6),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
        api.inflictTileStatusOnHand(
            RandomTileStatusInflictStrategy(
                TileStatus.SHOCK,
                1
            )
        )
    }
}
