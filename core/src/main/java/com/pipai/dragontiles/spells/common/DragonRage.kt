package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.spells.*

class DragonRage(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:DragonRage"
    override val requirement: ComponentRequirement = Sequential(9, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = 1

    companion object {
        val DRAGON_RAGE = Status("base:status:DragonRage", false)
    }

    override fun newClone(upgraded: Boolean): DragonRage {
        return DragonRage(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        if (api.fetchStatus(DRAGON_RAGE) == 0) {
            api.register(DragonRageImpl())
        }
        api.changeStatusIncrement(DRAGON_RAGE, if (upgraded) 3 else 2)
    }

    class DragonRageImpl {
        @CombatSubscribe
        fun onSpellCast(ev: SpellCastedEvent, api: CombatApi) {
            if (ev.spell.requirement.type == SetType.SEQUENTIAL) {
                val amount = api.fetchStatus(DRAGON_RAGE)
                api.changeStatusIncrement(Status.STRENGTH, amount)
            }
        }
    }
}

