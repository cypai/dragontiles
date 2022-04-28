package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.EnemyTurnEndEvent
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileStatus

class Sandstorm(amount: Int) : Status(amount) {
    override val id = "base:status:Sandstorm"
    override val assetName = "sandstorm.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Sandstorm(amount)
    }

    @CombatSubscribe
    suspend fun onEnemyTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        val list: MutableList<Tile> = mutableListOf()
        repeat(amount) {
            list.add(Tile.FumbleTile())
        }
        api.addTilesToHand(list, TileStatus.NONE, (combatant as Combatant.EnemyCombatant).enemy)
    }
}
