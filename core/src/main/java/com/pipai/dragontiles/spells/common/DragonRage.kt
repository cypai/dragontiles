package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.status.Strength

class DragonRage(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:DragonRage"
    override val requirement: ComponentRequirement = Sequential(9, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = 1

    override fun newClone(upgraded: Boolean): DragonRage {
        return DragonRage(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(DragonRageStatus(2))
    }

    class DragonRageStatus(amount: Int) : Status(amount) {
        override val displayAmount = true
        override val strId = "base:status:DragonRage"

        @CombatSubscribe
        fun onSpellCast(ev: SpellCastedEvent, api: CombatApi) {
            if (ev.spell.requirement.type == SetType.SEQUENTIAL) {
                api.addStatusToHero(Strength(amount))
            }
        }
    }
}

