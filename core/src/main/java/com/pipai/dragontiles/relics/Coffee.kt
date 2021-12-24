package com.pipai.dragontiles.relics

import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Coffee : Relic() {
    override val id = "base:relics:Coffee"
    override val assetName = "coffee.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    override fun onPickup(api: GlobalApi) {
        api.gainMaxFluxImmediate(3)
    }
}
