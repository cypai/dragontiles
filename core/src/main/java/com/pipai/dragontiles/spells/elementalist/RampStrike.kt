package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.*

class RampStrike(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:RampStrike"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = 1

    private var spellsCasted = 0

    override fun baseDamage(): Int = 3 + spellsCasted * (if (upgraded) 3 else 2)

    override fun newClone(upgraded: Boolean): RampStrike {
        return RampStrike(upgraded)
    }

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        spellsCasted = 0
    }

    @CombatSubscribe
    fun onSpellCast(ev: SpellCastedEvent, api: CombatApi) {
        spellsCasted += 1
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }
}
