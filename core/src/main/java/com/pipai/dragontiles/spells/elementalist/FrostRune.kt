package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class FrostRune : Rune() {
    override val id: String = "base:spells:FrostRune"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX(SuitGroup.ICE)
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        val freezes = api.combat.hand.filter { it.tileStatus == TileStatus.FREEZE }.size
        api.heroLoseFlux(freezes * components().size)
    }
}
