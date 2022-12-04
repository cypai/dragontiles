package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class QiRune : Rune() {
    override val id: String = "base:spells:QiRune"
    override val rarity: Rarity = Rarity.STARTER
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ANY_NO_FUMBLE)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        ScryAspect(1, autoDescription = false, autoCast = false)
    )

    @CombatSubscribe
    suspend fun onEndTurn(ev: TurnEndEvent, api: CombatApi) {
        api.scry(aspects.findAs(ScryAspect::class)!!.amount!!)
    }

}
