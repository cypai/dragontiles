package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*

class ElixirsToxicity : StandardSpell() {
    override val id: String = "base:spells:ElixirsToxicity"
    override val requirement: ComponentRequirement = AnyCombo(1)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
        LimitedRepeatableAspect(2),
        CountdownAspect(17, CountdownType.SCORE, this::cdCallback)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    private suspend fun cdCallback(api: CombatApi) {
        api.score()
    }
}
