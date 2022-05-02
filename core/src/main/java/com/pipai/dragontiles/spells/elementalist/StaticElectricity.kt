package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Enpowered
import com.pipai.dragontiles.status.SimpleStatus

class StaticElectricity : StandardSpell() {
    override val id: String = "base:spells:StaticElectricity"
    override val requirement: ComponentRequirement = AnyCombo(2, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(StaticElectricityStatus(1))
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
                api.attack(ev.enemy, Element.LIGHTNING, 4, listOf())
                api.inflictTileStatusOnHand(RandomTileStatusInflictStrategy(TileStatus.SHOCK, amount))
            }
        }

        @CombatSubscribe
        suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
            api.removeHeroStatus(id)
        }
    }
}
