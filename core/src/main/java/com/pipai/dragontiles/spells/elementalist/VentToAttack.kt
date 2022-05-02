package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Resistance
import com.pipai.dragontiles.status.SimpleStatus

class VentToAttack : StandardSpell() {
    override val id: String = "base:spells:VentToAttack"
    override val requirement: ComponentRequirement = Single(SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(VentToAttackStatus(3))
    }

    class VentToAttackStatus(amount: Int) : SimpleStatus("base:status:VentToAttack", "ventilation.png", true, amount) {
        @CombatSubscribe
        suspend fun onAttack(ev: SpellCastedEvent, api: CombatApi) {
            if (ev.spell.type == SpellType.ATTACK) {
                api.heroLoseFlux(amount)
            }
        }

        @CombatSubscribe
        suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
            api.removeHeroStatus(VentToAttackStatus::class)
        }
    }
}
