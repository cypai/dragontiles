package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.PlayerDamageEvent
import com.pipai.dragontiles.spells.Rarity

class YinYangOrb : Relic() {
    override val id = "base:relics:YinYangOrb"
    override val assetName = "yinyangorb.png"
    override val rarity = Rarity.RARE
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onLoseHp(ev: PlayerDamageEvent, api: CombatApi) {
        api.heroLoseFlux(ev.amount)
    }
}
