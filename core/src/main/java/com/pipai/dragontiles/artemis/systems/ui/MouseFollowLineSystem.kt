package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.LineComponent
import com.pipai.dragontiles.artemis.components.MouseFollowComponent
import com.pipai.dragontiles.artemis.components.TimerComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class MouseFollowLineSystem(private val config: GameConfig) : IteratingSystem(allOf()), InputProcessor {
    private val mLine by require<LineComponent>()
    private val mMouseFollow by require<MouseFollowComponent>()

    private val mouse = Vector2()

    override fun process(entityId: Int) {
        val cLine = mLine.get(entityId)
        cLine.end.set(mouse)
    }

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        mouse.x = screenX.toFloat()
        mouse.y = config.resolution.height - screenY.toFloat()
        return false
    }

    override fun scrolled(amount: Int) = false
}
