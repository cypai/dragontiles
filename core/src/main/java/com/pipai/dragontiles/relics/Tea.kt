package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Tea : Relic() {
    override val strId = "base:relics:Tea"
    override val assetName = "tea.png"
    override val rarity = Rarity.UNCOMMON

    override fun onPickup(api: GlobalApi) {
        api.gainMaxFluxImmediate(5)
    }
}
