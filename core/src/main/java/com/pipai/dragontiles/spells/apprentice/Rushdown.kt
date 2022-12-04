package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus

class Rushdown : StandardSpell() {
    override val id: String = "base:spells:Rushdown"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ANY_NO_FUMBLE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
    )

    override fun additionalLocalized(): List<String> {
        return listOf("base:status:Rushdown")
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(RushdownStatus(1))
    }

    class RushdownStatus(
        amount: Int,
    ) : SimpleStatus("base:status:Rushdown", "red.png", true, amount) {

        @CombatSubscribe
        suspend fun onCast(ev: SpellCastedEvent, api: CombatApi) {
            if (CombatFlag.ATTACK in ev.spell.flags()) {
                api.queryPoolDraw(1)
            }
        }

        @CombatSubscribe
        suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
            api.removeHeroStatus(id)
        }
    }
}
