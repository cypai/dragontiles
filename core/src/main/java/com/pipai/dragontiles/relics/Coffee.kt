package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Coffee : Relic() {
    override val strId = "base:relics:Coffee"
    override val assetName = "coffee.png"
    override val rarity = Rarity.COMMON

    override fun onPickup(api: GlobalApi) {
        api.gainMaxFlux(3)
    }
}
