package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SwapEvent
import com.pipai.dragontiles.spells.*

class ReturnToSender : StandardSpell() {
    override val id: String = "base:spells:ReturnToSender"
    override val requirement: ComponentRequirement = UnplayableRequirement()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(10, false),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    @CombatSubscribe
    suspend fun onSwap(ev: SwapEvent, api: CombatApi) {
        if (ev.activeSpells.contains(this)) {
            api.heroLoseFlux(baseFluxLoss())
        }
    }
}
