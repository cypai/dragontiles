package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class PiercingStrike : StandardSpell() {
    override val strId: String = "base:spells:PiercingStrike"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(3),
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), piercing = true)
    }
}
