package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class Purify : StandardSpell() {
    override val id: String = "base:spells:Purify"
    override val requirement: ComponentRequirement = AnyCombo(3)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.setTileStatus(components(), TileStatus.NONE)
    }

    override suspend fun handleComponents(api: CombatApi) {
        // Prevent consume
    }
}
