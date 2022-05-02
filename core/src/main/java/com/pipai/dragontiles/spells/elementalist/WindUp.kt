package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.SwapEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Keywords
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Enpowered
import com.pipai.dragontiles.utils.getStackableAmount

class WindUp : StandardSpell() {
    override val id: String = "base:spells:WindUp"
    override val requirement: ComponentRequirement = UnplayableRequirement()
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Enpowered(5, Element.NONE), 1),
    )

    override fun additionalKeywords(): List<String> {
        return listOf(Keywords.SWAP)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    @CombatSubscribe
    suspend fun onSwap(ev: SwapEvent, api: CombatApi) {
        api.addStatusToHero(Enpowered(aspects.getStackableAmount(Enpowered::class), Element.NONE))
    }
}
