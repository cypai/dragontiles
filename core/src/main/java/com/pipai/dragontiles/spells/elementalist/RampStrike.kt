package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class RampStrike : StandardSpell() {
    override val strId: String = "base:spells:RampStrike"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(3),
        FluxGainAspect(2),
    )

    private var spellsCasted = 0

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        aspects.findAs(AttackDamageAspect::class)!!.amount -= 3 * spellsCasted
        spellsCasted = 0
    }

    @CombatSubscribe
    fun onSpellCast(ev: SpellCastedEvent, api: CombatApi) {
        aspects.findAs(AttackDamageAspect::class)!!.amount += 3
        spellsCasted += 1
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }
}
