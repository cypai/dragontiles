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
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        XAspect(5, 0),
    )

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        val burns = api.getHandTiles().filter { it.tileStatus == TileStatus.BURN }.size
        val damage = x() * burns
        if (damage > 0) {
            api.aoeAttack(Element.FIRE, damage, flags())
        }
    }
}
