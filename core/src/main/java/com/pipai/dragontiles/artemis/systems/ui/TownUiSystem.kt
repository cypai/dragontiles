package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.ShopClickEvent
import com.pipai.dragontiles.artemis.screens.SpellShopScreen
import com.pipai.dragontiles.data.RunData
import net.mostlyoriginal.api.event.common.Subscribe

class TownUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData
) : BaseSystem() {

    override fun initialize() {
    }

    @Subscribe
    fun handleShopClick(ev: ShopClickEvent) {
        game.screen = SpellShopScreen(game, runData)
    }

    override fun processSystem() {
    }

}
