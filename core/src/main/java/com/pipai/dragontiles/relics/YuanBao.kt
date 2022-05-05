package com.pipai.dragontiles.relics

import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class YuanBao : Relic() {
    override val id = "base:relics:YuanBao"
    override val assetName = "yuan_bao.png"
    override val rarity = Rarity.RARE
    override val showCounter: Boolean = false

    override fun onPickup(api: GlobalApi) {
        api.gainGoldImmediate(9)
    }
}
