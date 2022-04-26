package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class VentingCycle : StandardSpell() {
    override val id: String = "base:spells:VentingCycle"
    override val requirement: ComponentRequirement = AnyCombo(2, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxLossAspect(4),
        TempMaxFluxGainAspect(4),
        FetchAspect(1),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.heroLoseFlux(baseFluxLoss())
        api.changeTemporaryMaxFlux(baseTempMaxFluxGain())
    }
}