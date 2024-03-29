package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class Vent : StandardSpell() {
    override val id: String = "base:spells:Vent"
    override val requirement: ComponentRequirement = AnyCombo(2, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(7),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }
}
