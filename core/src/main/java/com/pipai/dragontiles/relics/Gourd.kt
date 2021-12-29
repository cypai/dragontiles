package com.pipai.dragontiles.relics

import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Gourd : Relic() {
    override val id = "base:relics:Gourd"
    override val assetName = "gourd.png"
    override val rarity = Rarity.COMMON
    override val showCounter: Boolean = false

    override fun onPickup(api: GlobalApi) {
        api.gainPotionSlots(2)
    }
}
