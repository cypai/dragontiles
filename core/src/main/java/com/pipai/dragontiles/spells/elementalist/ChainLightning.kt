package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class ChainLightning : StandardSpell() {
    override val id: String = "base:spells:ChainLightning"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(13),
        FluxGainAspect(7),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
        if (api.combat.hand.isNotEmpty()) {
            if (api.combat.hand.size > 1) {
                val tile = api.queryTiles("Pick a tile to shock", api.combat.hand, 1, 1)
                api.setTileStatus(tile, TileStatus.SHOCK)
            } else {
                api.setTileStatus(api.combat.hand, TileStatus.SHOCK)
            }
        }
    }
}
