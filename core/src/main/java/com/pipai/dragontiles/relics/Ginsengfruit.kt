package com.pipai.dragontiles.relics

import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Ginsengfruit : Relic() {
    override val id = "base:relics:Ginsengfruit"
    override val assetName = "ginsengfruit.png"
    override val rarity = Rarity.RARE
    override val showCounter: Boolean = false

    override fun onPickup(api: GlobalApi) {
        api.gainMaxHpImmediate(13)
    }
}
