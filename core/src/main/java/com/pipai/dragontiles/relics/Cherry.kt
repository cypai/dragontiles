package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Cherry : Relic() {
    override val strId = "base:relics:Cherry"
    override val assetName = "cherry.png"
    override val rarity = Rarity.COMMON

    override fun onPickup(api: GlobalApi) {
        api.gainMaxHpImmediate(5)
    }
}
