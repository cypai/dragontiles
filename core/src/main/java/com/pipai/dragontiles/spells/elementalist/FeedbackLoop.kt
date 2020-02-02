package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.spells.*

class FeedbackLoop(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:FeedbackLoop"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ARCANE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): FeedbackLoop {
        return FeedbackLoop(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.changeStatusIncrement(Status.STRENGTH, api.combat.heroStatus[Status.STRENGTH])
    }
}