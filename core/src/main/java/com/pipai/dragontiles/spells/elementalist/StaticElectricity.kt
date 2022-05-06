package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.EnemyHitPlayerEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus

class StaticElectricity : PowerSpell() {
    override val id: String = "base:spells:StaticElectricity"
    override val requirement: ComponentRequirement = Sequential(2, SuitGroup.LIGHTNING)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(StaticElectricityStatus(5))
    }

    class StaticElectricityStatus(amount: Int) : SimpleStatus(
        "base:status:StaticElectricity",
        "yellow.png",
        true,
        amount,
    ) {

        @CombatSubscribe
        suspend fun onHit(ev: EnemyHitPlayerEvent, api: CombatApi) {
            if (CombatFlag.ATTACK in ev.flags) {
                val shocks = api.getHandTiles().filter { it.tileStatus == TileStatus.SHOCK }.size
                api.attack(ev.enemy, Element.LIGHTNING, amount * shocks, listOf())
            }
        }
    }
}
