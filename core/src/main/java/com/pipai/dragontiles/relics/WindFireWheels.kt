package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.EnemyDefeatedEvent
import com.pipai.dragontiles.spells.Rarity

class WindFireWheels : Relic() {
    override val id = "base:relics:WindFireWheels"
    override val assetName = "wind_fire_wheels.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onEnemyDefeat(ev: EnemyDefeatedEvent, api: CombatApi) {
        api.draw(3)
    }
}
