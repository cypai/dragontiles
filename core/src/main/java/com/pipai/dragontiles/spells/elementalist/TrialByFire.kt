package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.PlayerDamageEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.findAs

class TrialByFire : PowerSpell() {
    override val id: String = "base:spells:TrialByFire"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.FIRE)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Strength(1), 1),
        FluxGainAspect(7),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val amount = aspects.findAs(StackableAspect::class)!!.status.amount
        api.addStatusToHero(TrialByFireStatus(amount))
    }

    class TrialByFireStatus(amount: Int) :
        SimpleStatus("base:status:TrialByFire", "red.png", true, amount) {
        @CombatSubscribe
        suspend fun onBurn(ev: PlayerDamageEvent, api: CombatApi) {
            if (ev.flags.contains(CombatFlag.BURN)) {
                api.addStatusToHero(Strength(amount))
            }
        }
    }
}
