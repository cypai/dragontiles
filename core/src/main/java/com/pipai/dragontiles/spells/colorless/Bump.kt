package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.successor
import com.pipai.dragontiles.spells.*

class Bump : StandardSpell() {
    override val id: String = "base:spells:Bump"
    override val requirement: ComponentRequirement = ForbidTransformFreeze(
        this,
        Single(SuitGroup.ANY_NO_FUMBLE)
    )
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(1),
        TransformAspect(),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override suspend fun handleComponents(api: CombatApi) {
        val tile = components().first()
        api.transformTile(tile, successor(tile.tile), true)
    }
}
