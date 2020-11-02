package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.MouseFollowComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class MouseXySystem(private val config: GameConfig) : IteratingSystem(allOf()), InputProcessor {
    private val mXy by require<XYComponent>()
    private val mMouseFollow by require<MouseFollowComponent>()

    private val mouse = Vector2()

    override fun process(entityId: Int) {
        mXy.get(entityId).setXy(mouse)
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

    override fun scrolled(amountX: Float, amountY: Float) = false
}
