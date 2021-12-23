package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.findAsWhere

class DragonRage : StandardSpell() {
    override val strId: String = "base:spells:DragonRage"
    override val requirement: ComponentRequirement = Sequential(9, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(DragonRageStatus(2), 1)
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val stackable = aspects.findAsWhere(StackableAspect::class) { it.status is DragonRageStatus }!!
        api.addStatusToHero(stackable.status.deepCopy())
    }

    class DragonRageStatus(amount: Int) : Status(amount) {
        override val displayAmount = true
        override val assetName = "assets/binassets/graphics/status/dragon_rage.png"
        override val strId = "base:status:DragonRage"

        override fun deepCopy(): Status {
            return DragonRageStatus(amount)
        }

        @CombatSubscribe
        suspend fun onSpellCast(ev: SpellCastedEvent, api: CombatApi) {
            if (ev.spell.requirement.type == SetType.SEQUENTIAL) {
                api.addStatusToHero(Strength(amount))
            }
        }
    }
}

