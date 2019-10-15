package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.DrawPostEvent

class Transmuter : Relic() {
    override val id = "base:Transmuter"

    @CombatSubscribe
    suspend fun postDraw(ev: DrawPostEvent, api: CombatApi) {
        api.queryTiles("Transform?", ev.tiles.map { it.first }, 0, 1)
    }
}
