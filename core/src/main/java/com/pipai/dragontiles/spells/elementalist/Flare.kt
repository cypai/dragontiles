package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class Flare : StandardSpell() {
    override val id: String = "base:spells:Flare"
    override val requirement: ComponentRequirement = Single(SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val glowType: GlowType = GlowType.FIRE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2),
        FluxGainAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, Element.FIRE, baseDamage(), flags())
        if (api.combat.pool.isNotEmpty()) {
            if (api.combat.pool.size > 1) {
                val tile = api.queryTiles("Choose a tile to burn", api.combat.pool, 1, 1)
                api.setTileStatus(tile, TileStatus.BURN)
            } else {
                api.setTileStatus(api.combat.pool, TileStatus.BURN)
            }
        }
    }
}
