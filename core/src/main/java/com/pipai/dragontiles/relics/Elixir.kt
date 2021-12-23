package com.pipai.dragontiles.relics

import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import kotlinx.serialization.Serializable

class Elixir : Relic() {
    override val id = "base:relics:Elixir"
    override val assetName = "elixir.png"
    override val rarity = Rarity.RARE

    override fun onPickup(api: GlobalApi) {
        api.gainMaxFluxImmediate(7)
    }
}
