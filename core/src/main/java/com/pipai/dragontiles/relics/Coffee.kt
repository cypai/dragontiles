package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import kotlinx.serialization.Serializable

class Coffee : Relic() {
    override val id = "base:relics:Coffee"
    override val assetName = "coffee.png"
    override val rarity = Rarity.COMMON

    override fun onPickup(api: GlobalApi) {
        api.gainMaxFluxImmediate(3)
    }
}
