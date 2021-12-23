package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import kotlinx.serialization.Serializable

class Peach : Relic() {
    override val strId = "base:relics:Peach"
    override val assetName = "peach.png"
    override val rarity = Rarity.UNCOMMON

    override fun onPickup(api: GlobalApi) {
        api.gainMaxHpImmediate(9)
    }
}