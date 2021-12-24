package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import kotlinx.serialization.Serializable

class Cherry : Relic() {
    override val id = "base:relics:Cherry"
    override val assetName = "cherry.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    override fun onPickup(api: GlobalApi) {
        api.gainMaxHpImmediate(5)
    }
}
