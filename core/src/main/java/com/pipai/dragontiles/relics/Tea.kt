package com.pipai.dragontiles.relics

import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Tea : Relic() {
    override val id = "base:relics:Tea"
    override val assetName = "tea.png"
    override val rarity = Rarity.UNCOMMON
    override val showCounter: Boolean = false

    override fun onPickup(api: GlobalApi) {
        api.gainMaxFluxImmediate(5)
    }
}
