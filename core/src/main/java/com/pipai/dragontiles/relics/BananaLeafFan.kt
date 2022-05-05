package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TileTransformedEvent
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.Rarity

class BananaLeafFan : Relic() {
    override val id = "base:relics:BananaLeafFan"
    override val assetName = "banana_leaf_fan.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    @CombatSubscribe
    suspend fun onTransform(ev: TileTransformedEvent, api: CombatApi) {
        api.setTileStatus(listOf(ev.tile), TileStatus.NONE)
    }
}
