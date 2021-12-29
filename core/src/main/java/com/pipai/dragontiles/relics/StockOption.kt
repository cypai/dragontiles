package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.BattleWinEvent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.spells.Rarity

class StockOption : Relic() {
    override val id = "base:relics:StockOption"
    override val assetName = "stock_option.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    fun onBattleWin(ev: BattleWinEvent, api: CombatApi) {
        api.gainGoldImmediate(2)
        if (api.runData.hero.gold >= 20) {
            api.gainGoldImmediate(-15)
        }
    }
}
