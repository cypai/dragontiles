package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.ComponentConsumeEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class ThunderRune : Rune() {
    override val id: String = "base:spells:ThunderRune"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = IdenticalX(SuitGroup.LIGHTNING)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        XAspect(0),
    )

    @CombatSubscribe
    suspend fun onConsume(ev: ComponentConsumeEvent, api: CombatApi) {
        val shocks = ev.components.filter { it.tileStatus == TileStatus.SHOCK }.size
        val damage = 13 * x() * shocks
        if (damage > 0) {
            api.aoeAttack(Element.LIGHTNING, damage, flags())
        }
    }
}
