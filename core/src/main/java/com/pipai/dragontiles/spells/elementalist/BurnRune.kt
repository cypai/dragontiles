package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class BurnRune : Rune() {
    override val id: String = "base:spells:BurnRune"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX(SuitGroup.FIRE)
    override val aspects: MutableList<SpellAspect> = mutableListOf()

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        val burns = api.combat.hand.filter { it.tileStatus == TileStatus.BURN }.size
        api.aoeAttack(Element.FIRE, components().size * burns, flags())
    }
}
