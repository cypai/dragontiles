package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.MainMenuScreen
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.utils.getSystemSafe
import com.pipai.dragontiles.utils.system

class PauseMenuSystem(
    private val game: DragonTilesGame,
    var stage: Stage,
    private val runData: RunData,
    private var allowShow: Boolean,
) : NoProcessingSystem(), InputProcessor {

    private val skin = game.skin

    private val table = Table()
    private val yesBtn = TextButton(" Yes ", skin)
    private val noBtn = TextButton(" No ", skin)

    private var showing = false

    override fun initialize() {
        table.background = skin.getDrawable("frameDrawable")
        table.add(Label("Return to main menu?", skin))
            .colspan(2)
        table.row()
        table.add(yesBtn)
        table.add(noBtn)
        table.row()
        val resolution = game.gameConfig.resolution
        table.width = resolution.width / 3f
        table.height = resolution.height / 6f
        table.x = (resolution.width - table.width) / 2f
        table.y = resolution.height / 2f

        yesBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game)
            }
        })
        noBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                returnToUi()
            }
        })
    }

    private fun returnToUi() {
        table.remove()
        showing = false
        if (!runData.combatWon) {
            world.getSystemSafe(CombatUiSystem::class.java)?.enable()
        }
    }

    fun enable() {
        allowShow = true
    }

    fun disable() {
        allowShow = false
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            if (showing) {
                returnToUi()
            } else {
                if (allowShow) {
                    world.getSystemSafe(CombatUiSystem::class.java)?.disable()
                    showing = true
                    stage.addActor(table)
                }
            }
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = false

    override fun scrolled(amountX: Float, amountY: Float) = false
}
