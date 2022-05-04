package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.BattleWinEvent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.spells.*

class PotOfGreed : Rune() {
    override val id: String = "base:spells:PotOfGreed"
    override val rarity: Rarity = Rarity.SPECIAL
    override val requirement: ComponentRequirement = Identical(3)
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        NotManuallyDeactivateable(),
    )

    @CombatSubscribe
    suspend fun onBattleWin(ev: BattleWinEvent, api: CombatApi) {
        if (this.active) {
            api.gainGoldImmediate(1)
        }
    }

}
