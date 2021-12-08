package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.ShopClickEvent
import com.pipai.dragontiles.artemis.screens.ShopScreen
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.dungeon.RunData
import net.mostlyoriginal.api.event.common.Subscribe

class ShopUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData
) : BaseSystem(), InputProcessor {

    override fun initialize() {
    }

    override fun processSystem() {
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            game.screen = TownScreen(game, runData, false)
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false
}
