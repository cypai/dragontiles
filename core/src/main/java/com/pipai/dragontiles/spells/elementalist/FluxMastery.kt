package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.*

class FluxMastery : PowerSpell() {
    override val id: String = "base:spells:FluxMastery"
    override val requirement: ComponentRequirement = RainbowIdentical(3)
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf()
    override val scoreable: Boolean = true

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val ref = instanceRef
        if (ref != null) {
            ref.permanentValue += 2
        }
    }

    override fun dynamicCustom(): Int {
        return 5 + (instanceRef?.permanentValue ?: 0)
    }

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1) {
            api.changeTemporaryMaxFlux(dynamicCustom())
        }
    }
}
