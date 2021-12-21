package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class Ginsengfruit : Relic() {
    override val strId = "base:relics:Ginsengfruit"
    override val assetName = "ginsengfruit.png"
    override val rarity = Rarity.RARE

    override fun onPickup(api: GlobalApi) {
        api.gainMaxHp(13)
    }
}
